package com.yx.leetcode;

public class move_zeros {

    public static void main(String[] args) throws InterruptedException {
        byte[] tmp = new byte[1024*1024*34];
//        byte[] tmp2 = new byte[1024];
        testtt t = new testtt();
            Thread.sleep(100000000);
//        Solution3 s = new Solution3();
//        int[] nums = new int[]{0,0,0,1,2,2,1,0,0,0,0,1,0,3,12};
//        s.moveZeroes(nums);
//
//        for (int a:nums
//             ) {
//            System.out.print(a+" ");
//        }
//        tmp[1] = 1;
//        tmp2[1] = 1;

    }
}

class testtt{
    private static final byte[] tmp;

    static {
        tmp = new byte[1024*1024*33];
    }
}

class Solution3 {
    public void moveZeroes(int[] nums) {
        int indexa = 0;
        int indexb = 0;
        while(nums[indexb]!=0)
            indexb++;
        while(indexa<nums.length-1&&indexb<nums.length){
            while(indexa<nums.length-1){
                if(nums[indexa] != 0){
                    indexa++;
                }else{
                    break;
                }
            }
            while(indexb<nums.length-1){
                if(nums[indexb] ==0){
                    indexb++;
                }else{
                    break;
                }
            }
//            if(indexa>indexb){
//                indexa++;
//            }else{
                int tmp = nums[indexa];
                nums[indexa] = nums[indexb];
                nums[indexb] = tmp;
                indexa++;
                indexb++;
//            }
        }
        // return nums;
    }
}
