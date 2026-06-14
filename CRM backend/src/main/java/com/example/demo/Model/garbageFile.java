package com.example.demo.Model;
//import javafx.util.Pair;

import java.util.*;

public class garbageFile {// or put it in a utility package

    public static void main(String[] args){
        System.out.println(Voting(4,4,new int[]{0,0,1,0},new int[]{1,10,5,10}));
    }
    public static int Voting(int n,int k,int[] v,int[] w){
        int person1 =0,person2=0,K=k;
        HashSet<Integer> set = new HashSet<>();
        for(int i=0;i<v.length;i++){
            if(v[i]==1)
                person1+=w[i];
            else {
                person2 += w[i];
                set.add(i);
            }
        }
        if(person1>person2)
            return 0;
        while(k>0 && !set.isEmpty()){
            int max =0,ind=0;
            for(Integer i: set){
                if(w[i]>max) {
                    max = w[i];
                    ind=i;
                }
            }
            k--;
            v[ind]=1;
            person1+=w[ind];
            person2-=w[ind];
            set.remove(ind);
            if(person1>person2)
                return K-k;
        }
        return -1;
    }
}
