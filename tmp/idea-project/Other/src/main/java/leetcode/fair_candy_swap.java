package leetcode;

import java.util.HashMap;
import java.util.Map;

public class fair_candy_swap {

}

class Solution22 {
    public int[] fairCandySwap(int[] A, int[] B) {
        Map<Integer,Integer> mapb = new HashMap();
        for(int i=0; i < B.length; i++){
            mapb.put(B[i],i);
        }
        int total = 0;
        int totala = 0;
        for(int i=0; i < A.length; i++){
            total+=A[i];
        }
        totala = total;
        for(int i=0; i < B.length; i++){
            total+=B[i];
        }
        total = total/2;
        totala = total - totala;
        int[] ans = new int[2];

        for(int i=0; i < A.length; i++){
            if(mapb.containsKey(totala+A[i])){
                ans[0] = A[i];
                ans[1] = totala+A[i];
                return ans;
            }
        }
        return ans;
    }
}