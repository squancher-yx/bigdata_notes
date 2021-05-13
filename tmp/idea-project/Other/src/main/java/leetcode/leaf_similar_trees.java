package leetcode;

public class leaf_similar_trees {
    public static void main(String[] args) {

    }
    static String res1 = "";
    static String res2 = "";
    class Solution {
        public boolean leafSimilar(TreeNode root1, TreeNode root2) {
            res1 = "";
            res2 = "";
            search1(root1);
            search2(root2);
            System.out.println(res1);
            System.out.println(res2);
            return res1.equals(res2);
        }
    }

    public static void search1(TreeNode node) {
        if (node == null)
            return;

        if (node.left != null && node.right != null) {
            res1 = res1 + "," + node.val;
        }
        if (node.left != null) {
            search1(node.left);
        }
        if (node.right != null) {
            search1(node.right);
        }
    }

    public static void search2(TreeNode node) {
        if (node == null)
            return;

        if (node.left != null && node.right != null) {
            res2 = res2 + "," + node.val;
        }
        if (node.left != null) {
            search2(node.left);
        }
        if (node.right != null) {
            search2(node.right);
        }
    }

    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
}


//6
//7
//14
//9
//8
//6
//71
//4
//9
//8
