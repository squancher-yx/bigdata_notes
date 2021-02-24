package leetcode;

import java.util.ArrayList;
import java.util.List;

public class positions_of_large_groups {
    public static void main(String[] args) {
        Solution4 s = new Solution4();
        int MOD = 1_000_000_007;
        System.out.println(MOD);
        s.largeGroupPositions("abbxxxxzzy");
    }
}

class Solution4 {
    public List<List<Integer>> largeGroupPositions(String s) {
        List<List<Integer>> res = new ArrayList<List<Integer>>();
        int start = 0;
        int end = 0;
        char pre = s.charAt(0);
        for(int i = 1; i < s.length(); i++){
            if(pre == s.charAt(i)){
                end = i;
                pre = s.charAt(i);
            }else{
                if(end-start>=2){
                    List<Integer> tmp = new ArrayList<Integer>();
                    tmp.add(start);
                    tmp.add(end);
                    res.add(tmp);
                }
                pre = s.charAt(i);
                start = i;
                end = i;
            }
        }
        return res;
    }
}