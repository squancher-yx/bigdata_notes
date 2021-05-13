package leetcode;

public class increasing_order_search_tree {
    
}

class Solution333 {
    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    TreeNode res = new TreeNode();
    public TreeNode increasingBST(TreeNode root) {
        search(root);
        return res;
    }

    public void search(TreeNode root){
        if(root == null){
            return;
        }
        search(root.left);
        res.right = new TreeNode(root.val);
        search(root.right);
    }
}


