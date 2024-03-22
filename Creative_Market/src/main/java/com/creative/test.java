package com.creative;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
//        Map<Integer, Double> weightsMap = new HashMap<>();
//        weightsMap.put(6, 0.38095238095238093);
//        weightsMap.put(7, 0.38095238095238093);
//        weightsMap.put(9, 0.0);
//        weightsMap.put(8, 0.0);
//        weightsMap.put(4, 0.23809523809523803);
//
//        test lottery = new test(weightsMap);
//        int winner = (int) lottery.draw();
//        System.out.println("Winner is: " + winner);

        String ids="1,2,3,4,5";
        ArrayList<Integer> list=new ArrayList<>();
        String[] split = ids.split(",");
        for (int i = 0; i < split.length; i++) {
            list.add(Integer.parseInt(split[i]));
        }
        System.out.println(list);
    }
}
