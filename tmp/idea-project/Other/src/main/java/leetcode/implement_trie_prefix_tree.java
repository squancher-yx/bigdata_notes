package leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class implement_trie_prefix_tree {

    public static void main(String[] args) {
        Trie test = new Trie();
        test.insert("app");
        System.out.println(test.search("app"));
    }
}

class Tree {
    boolean isEnd;
    HashMap<Character, Tree> data;

    public Tree() {
        this.data = new HashMap<>();
    }
}

class Trie {
    private Tree tree;

    /**
     * Initialize your data structure here.
     */
    public Trie() {
        tree = new Tree();
    }

    /**
     * Inserts a word into the trie.
     */
    public void insert(String word) {
        Tree tmp = this.tree;
        for (int i = 0; i < word.length(); i++) {
            Character ch = word.charAt(i);
            if (!tmp.data.containsKey(ch)) {
                tmp.data.put(ch, new Tree());
            }
            tmp = tmp.data.get(ch);
        }
        tmp.isEnd = true;
//        System.out.println("234");
    }

    /**
     * Returns if the word is in the trie.
     */
    public boolean search(String word) {
        Tree tmp = this.tree;
        for (int i = 0; i < word.length(); i++) {
            Character ch = word.charAt(i);
            if (!tmp.data.containsKey(ch)) {
                return false;
            }
            tmp = tmp.data.get(ch);
        }
        if(tmp.isEnd){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Returns if there is any word in the trie that starts with the given prefix.
     */
    public boolean startsWith(String prefix) {
        Tree tmp = this.tree;
        for (int i = 0; i < prefix.length(); i++) {
            Character ch = prefix.charAt(i);
            if (!tmp.data.containsKey(ch)) {
                return false;
            }
            tmp = tmp.data.get(ch);
        }
        return true;
    }
}

