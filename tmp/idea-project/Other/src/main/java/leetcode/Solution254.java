package leetcode;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;

public class Solution254 {
    public static void main(String[] args) {
        Solution111 tt = new Solution111();
        List<List<Integer>> res= tt.getFactors(32);
        System.out.println("123");
    }
}



class Solution111 {
    public List<List<Integer>> getFactors(int n) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        helper(result, new ArrayList<Integer>(), n, 2);
        return result;
    }

    public void helper(List<List<Integer>> result, List<Integer> item, int n, int start){


        for (int i = start; i  <= sqrt(n); ++i) {
            if (n % i != 0){
                continue;
            }
//            if (n % i == 0) {
                item.add(i);
                helper(result, item, n/i, i);
                item.remove(item.size()-1);
//            }
        }

        item.add(n);
        //去除本身
        if (item.size() > 1) {
            result.add(new ArrayList<Integer>(item));
        }
        item.remove(item.size()-1);
    }
}


class Solution222 {
    public List<List<Integer>> getFactors(int n) {
        List<List<Integer>> res =  new ArrayList<List<Integer>>();
        helper(n, 2, new ArrayList<Integer>(), res);
        return res;
    }
    void helper(int n, int start, List<Integer> out, List<List<Integer>> res) {

        for (int i = start; i *i<= n; ++i) {
            if (n % i != 0){
                continue;
            }
            out.add(i);

            out.add(n/i);
            res.add(new ArrayList<Integer>(out));
            out.remove(out.size()-1);

            helper(n / i, i, out, res);
            out.remove(out.size()-1);
        }
    }
}