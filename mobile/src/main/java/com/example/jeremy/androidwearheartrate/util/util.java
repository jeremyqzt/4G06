package com.example.jeremy.androidwearheartrate.util;

import java.util.ArrayList;

/**
 * Created by kevind on 2017-03-09.
 */

public class util {
    public static double findMean(ArrayList<Integer> list){
        if(list.size() < 1){
            return 0.0;
        }
        double sum = 0.0;
        for(Integer i:list){
            sum+=i;
        }
        double result = sum/list.size();
        return result;
    }

    public static double findSTD(ArrayList<Integer> list){
        if(list.size() < 1){
            return 0.0;
        }
        double innersum = 0.0;
        double mean = findMean(list);
        for(Integer i:list){
            innersum += Math.pow(mean-i,2);
        }
        double result = Math.sqrt(innersum/list.size());
        return result;
    }

    public static boolean isIncreasing(ArrayList<Integer> list, int start){
        //start Index is how many indices from the last element to start checking
        int startIndex = list.size()-start;
        for(int i = startIndex;i < list.size()-1;i++){
            if(list.get(i) > list.get(i+1)){
                return false;
            }
        }
        return true;
    }

}
