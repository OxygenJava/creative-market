package com.creative.utils;

import com.creative.domain.commodity;

import java.util.Map;
import java.util.Random;

public class weightUtils {
    private Map<commodity, Double> weightsMap;
    private Random random;

    public weightUtils(Map<commodity, Double> weightsMap) {
        this.weightsMap = weightsMap;
        this.random = new Random();
    }

    public commodity draw() {
        double totalWeight = 0.0;
        for (double weight : weightsMap.values()) {
            totalWeight += weight;
        }
        double randomValue = random.nextDouble() * totalWeight * 1.05;
        double cumulativeWeight = 0.0;
        for (Map.Entry<commodity, Double> entry : weightsMap.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (randomValue < cumulativeWeight) {
                return entry.getKey();
            }
        }
        return null;
    }
}
