package leetcode;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class largest_number {
    static class Solution {
        public String largestNumber(int[] nums) {
            if (nums == null || nums.length == 0) {
                return "";
            }
            IntStream stream = Arrays.stream(nums);
            Stream<Integer> integerStream = stream.boxed();
            Integer[] integers = integerStream.toArray(Integer[]::new);

            Arrays.sort(integers, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    System.out.println(o1+""+o2+"    "+o2+""+o1);
                    if ((o1 + "" + o2).compareTo(o2 + "" + o1) > 0){
                        return -1;
                    }else{
                        return 1;
                    }
                }
            });
            String res = "";
            for (int i = 0; i < integers.length; i++) {
                res+=integers[i];
            }
            return res;
        }
    }
    public static void main(String[] args) {
        Solution s = new Solution();
        int[] nums = {3,2,1,5,9};
        ;
        System.out.println(s.largestNumber(nums));
    }
}

