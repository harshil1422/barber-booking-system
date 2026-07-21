package com.newlook.booking.controller;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        System.out.println("hello");

        final int num =1;
              int num2 =2;

        List<Integer> integers = Arrays.asList(1, 2, 5, 6, 7);
        integers=  integers.stream().map(e->e*num2).collect(Collectors.toList());
        System.out.println(integers);
        IntStream.range(1,6).boxed().map(e->e*num2).collect(Collectors.toList());
        System.out.println(integers);

        Function<String,Integer> getInt=( name) ->{
            return name.length();
        };
        int [] nub ={1,2,3,4,5,6,7};
        rotateArray(nub,4);
//        System.out.println(getInt.apply("harshi;"));
    }

    //rotate array with k step  using java

   public static  int[] rotateArray(int[] numbers, int k){
        if(k>numbers.length)
            k=k%numbers.length;

        int[] result = new int[numbers.length];

        for(int i=0; i<=numbers.length-k; i++){
            result[i]=numbers[k+1];
        }
       Arrays.stream(result).forEach(System.out::println);
       return  result;
   }

}
