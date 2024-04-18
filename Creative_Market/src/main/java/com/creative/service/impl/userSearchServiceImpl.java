package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.creative.domain.commodity;
import com.creative.domain.commodityHomePage;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.dto.userSearchDTO;
import com.creative.service.commodityHomePageService;
import com.creative.service.commodityService;
import com.creative.service.userSearchService;
import com.creative.utils.imgUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class userSearchServiceImpl implements userSearchService {

    @Autowired
    private commodityService commodityService;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private commodityHomePageService commodityHomePageService;

    //自定义设置高亮
    private final String heightLight = "<font style='color: red';>";
    private final String  endHeightLight= "</font>";

    @Value("${creativeMarket.shopImage}")
    private String imageAddress;
    @Override
    public Result getSearchInfo(userSearchDTO userSearch) throws IOException {
        //检查参数
        if (userSearch == null || "".equals(userSearch.getSearchInfo())){
            return Result.fail(Code.SYNTAX_ERROR,"搜索数据不能为空");
        }

        //设置查询条件
        SearchRequest searchRequest = new SearchRequest("app_seacher");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //关键字的分词之后查询
        QueryStringQueryBuilder description = QueryBuilders.queryStringQuery(userSearch.getSearchInfo())
                                                            .field("description")
                                                            .field("label");
        boolQueryBuilder.must(description);


        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("description");
        highlightBuilder.preTags("<font style='color: red';>");
        highlightBuilder.postTags("</font>");
        sourceBuilder.highlighter(highlightBuilder);

        sourceBuilder.query(boolQueryBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        SearchHit[] hits = search.getHits().getHits();
        List<Map> mapList = new ArrayList<>();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            Map map = JSON.parseObject(sourceAsString, Map.class);
            //处理高亮
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields != null && !highlightFields.isEmpty()){
                Text[] descriptions = highlightFields.get("description").getFragments();
                String join = StringUtils.join(descriptions);
                //高亮标题
                map.put("h_description",join);
            }else {
                //原始标题
                map.put("h_description",map.get("description"));

            }
            mapList.add(map);
        }

        //按照商品描述查询
        //数据库模糊查询
        List<commodityHomePage> descriptionList = commodityHomePageService.lambdaQuery().like(commodityHomePage::getDescription, userSearch.getSearchInfo()).list();
        //按照商品标签查询
        List<commodityHomePage> labelList = commodityHomePageService.lambdaQuery().like(commodityHomePage::getLabel, userSearch.getSearchInfo()).list();
        descriptionList.addAll(labelList);
        for (commodityHomePage commodityHomePage : descriptionList) {
            Map<String, Object> stringObjectMap = BeanUtil.beanToMap(commodityHomePage);
            String description1 = stringObjectMap.get("description").toString();
            //设置高亮
            StringBuilder sb = new StringBuilder();

            int index = description1.indexOf(userSearch.getSearchInfo());
            if (index == -1){
                String lowerCase = userSearch.getSearchInfo().toLowerCase();
                int i = description1.indexOf(lowerCase);
                if (i != -1){
                    index = i;
                }else {
                    String upperCase = userSearch.getSearchInfo().toUpperCase();
                    index = description1.indexOf(upperCase);
                }

            }

            try {
                String substring = description1.substring(0, index);
                sb.append(substring);
                sb.append(heightLight);
                int index2 = index + userSearch.getSearchInfo().length();
                sb.append(description1.substring(index,index2));
                sb.append(endHeightLight);
                sb.append(description1.substring(index2));
                stringObjectMap.put("h_description",sb);
            } catch (StringIndexOutOfBoundsException siobe) {
                siobe.printStackTrace();
            }
            String image = imgUtils.encodeImageToBase64(imageAddress+"\\"+stringObjectMap.get("homePageImage"));
            stringObjectMap.put("homePageImage",image);
            mapList.add(stringObjectMap);
        }
        //去重
        List<Map> distinct = distinct(mapList);

        //排序
        distinct.sort((o1, o2) -> {
                Integer commodityId = (Integer) o1.get("commodityId");
                // 时间原点
                LocalDateTime startTime = LocalDateTime.of(1970, 1, 1, 0, 0, 0);

                commodity one = commodityService.lambdaQuery().eq(commodity::getId, commodityId).one();
                long until = startTime.until(one.getReleaseTime(), ChronoUnit.SECONDS);

                Integer commodityId2 = (Integer) o2.get("commodityId");
                commodity one1 = commodityService.lambdaQuery().eq(commodity::getId, commodityId2).one();
                long until1 = startTime.until(one1.getReleaseTime(), ChronoUnit.SECONDS);

                return (int) (until1 - until);
        });

        //分页查询
        List<Map> collect = distinct.stream().skip(((long) (userSearch.getPageNumber() - 1) * userSearch.getPageSize()))
                .limit(userSearch.getPageSize())
                .collect(Collectors.toList());
        System.out.println(collect.size());
        return Result.success(collect);
    }

    /**
     * 集合去重
     * @param mapList
     */
    private List<Map> distinct(List<Map> mapList) {
        // 辅助HashSet用于存储已经出现过的id值
        Set<Object> seenIds = new HashSet<>();
        // 用于存储去重后的Map对象
        List<Map> distinctMapList = new ArrayList<>();

        // 遍历mapList中的每个Map对象
        for (Map map : mapList) {
            // 获取当前Map对象的id值
            Object id = map.get("id");
            // 如果当前id值在HashSet中已经存在，说明该Map对象是重复的，跳过此次循环
            if (seenIds.contains(id)) {
                continue;
            }
            // 将当前id值添加到HashSet中，表示已经处理过该id值
            seenIds.add(id);
            // 将当前Map对象添加到去重后的List中
            distinctMapList.add(map);
        }
        return distinctMapList;
    }
}
