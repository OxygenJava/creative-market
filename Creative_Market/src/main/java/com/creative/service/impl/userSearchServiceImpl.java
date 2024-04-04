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
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            StringBuilder sb = new StringBuilder();

            mapList.add(stringObjectMap);
        }

        //排序
        mapList.sort((o1, o2) -> {
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
        List<Map> collect = mapList.stream().skip(((long) (userSearch.getPageNumber() - 1) * userSearch.getPageSize()))
                .limit(userSearch.getPageSize())
                .collect(Collectors.toList());
        System.out.println(collect.size());
        return Result.success(collect);
    }

    public static void main(String[] args) {
        String heightLight = "<font style='color: red';>";
        String  endHeightLight= "</font>";
        String s = "盯盯拍行车记录仪MINI3S升级版 3K高清影像 超大存储拓展 AI驾驶辅助";
        String s1 = "行车记录仪";
        StringBuilder sb = new StringBuilder();
        char[] chars = s1.toCharArray();
        int i = s.indexOf(s1);
        int i1 = s.indexOf(chars[chars.length - 1]);
        char[] chars1 = s.toCharArray();
        for (int i2 = 0; i2 < chars1.length; i2++) {
            if (i2 == i){
                sb.append(heightLight);
            }
            if (i2 == i1){
                sb.append(endHeightLight);
            }
            sb.append(chars1[i2]);
        }
        System.out.println(sb);
    }
}
