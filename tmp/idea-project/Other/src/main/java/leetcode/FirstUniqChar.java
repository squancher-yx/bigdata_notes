package leetcode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FirstUniqChar {
    public static void main(String[] args) {
        firstUniqChar("qqwwwweettttrr");
    }

    public static int firstUniqChar(String s) {
        Set<Character> set = new HashSet<Character>();
        Map<Character, Integer> res = new HashMap<Character, Integer>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (set.contains(c)) {
                if (res.containsKey(c)) {
                    res.remove(c);
                }
            } else {
                set.add(c);
                res.put(c, i);
            }
        }

        if (res.size()==0)
            return -1;
        int min = 0xfffffff;
        for (Map.Entry<Character, Integer> en : res.entrySet()) {
            int value = en.getValue();
            if(value<min){
                min = value;
            }
            System.out.println(en.getKey() + "   " + en.getValue());
        }

        System.out.println(min);
        return min;
    }

}

