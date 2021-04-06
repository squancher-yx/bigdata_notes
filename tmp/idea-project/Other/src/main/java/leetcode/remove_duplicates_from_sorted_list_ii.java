package leetcode;

import org.jetbrains.annotations.NotNull;
import org.omg.PortableInterceptor.INACTIVE;

import java.util.Arrays;
import java.util.HashMap;

public class remove_duplicates_from_sorted_list_ii {

}


class Solution {
    class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    public ListNode deleteDuplicates(ListNode head) {
        HashMap<Integer,ListNode> map = new HashMap<Integer, ListNode>();
        while(head!=null){
            map.put(head.val,head);
        }



        return null;
    }
}