package com.creative;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creative.domain.lable;

import java.time.LocalDateTime;
import java.util.*;

public class test {
    private Map<Integer, Double> weightsMap;
    private Random random;

    public test(Map<Integer, Double> weightsMap) {
        this.weightsMap = weightsMap;
        this.random = new Random();
    }

    public Object draw() {
        double totalWeight = 0.0;
        for (double weight : weightsMap.values()) {
            totalWeight += weight;
        }
        System.out.println(totalWeight);

        double randomValue = random.nextDouble() * totalWeight * 1.5;
        System.out.println(randomValue);
        double cumulativeWeight = 0.0;

        for (Map.Entry<Integer, Double> entry : weightsMap.entrySet()) {
            cumulativeWeight += entry.getValue();
            System.out.println(cumulativeWeight);
            if (randomValue < cumulativeWeight) {
                return entry.getKey();
            }
        }

        // Should not reach here, but return -1 if it does
        return -1;
    }



    public static void main(String[] args) {
//        LocalDateTime localDateTime = LocalDateTime.now();
//        LocalDateTime localDateTime1 = localDateTime.plusDays(14);
//        System.out.println(localDateTime1);
        ArrayList<Integer> list=new ArrayList();
        list.add(1);
        list.add(2);
    }
}
