//package leetcode;
//
//import java.util.*;
//
//public class chuan_di_xin_xi {
//    class Solution {
//        int ways, n, k;
//        Map<List<Integer>> edges;
//
//        public int numWays(int n, int[][] relation, int k) {
//            ways = 0;
//            this.n = n;
//            this.k = k;
//            edges = new ArrayList<List<Integer>>();
//            for (int i = 0; i < n; i++) {
//                edges.add(new ArrayList<Integer>());
//            }
//            for (int[] edge : relation) {
//                int src = edge[0], dst = edge[1];
//                edges.get(src).add(dst);
//            }
//            dfs(0, 0);
//            return ways;
//        }
//
//        public void dfs(int index, int steps) {
//            if (steps == k) {
//                if (index == n - 1) {
//                    ways++;
//                }
//                return;
//            }
//            List<Integer> list = edges.get(index);
//            for (int nextIndex : list) {
//                dfs(nextIndex, steps + 1);
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//
//    }
//}
