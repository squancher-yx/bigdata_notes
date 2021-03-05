package leetcode;

import java.util.Arrays;
import java.util.Comparator;

public class russian_doll_envelopes {
    class Solution {
        public int maxEnvelopes(int[][] envelopes) {
            Arrays.sort(envelopes, new Comparator<int[]>() {
                @Override
                public int compare(int[] o1, int[] o2) {
                    if (o1[0]!=o2[0]){
                        return o1[0]-o2[0];
                    }else{
                        return o1[1]-o2[1];
                    }

                }
            });

            for (int i = 0; i < envelopes.length; i++) {
                System.out.println(envelopes[i][0]+"  "+envelopes[i][1]);
            }
            return 0;
        }
    }
}


