package leetcode;

public class odd_even_linked_list {

}

class Solution2 {
    public ListNode oddEvenList(ListNode head) {
        ListNode pointPre = head;
        ListNode pointNext = head.next;
        while (pointNext.next != null) {
            ListNode tmp = pointNext.next.next;
            pointPre.next = pointNext.next;
            pointNext.next = tmp;
            pointPre.next.next = pointNext;
            pointNext = pointNext.next.next;
            pointPre = pointPre.next;
        }
        return null;
    }
}

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