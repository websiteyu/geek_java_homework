package com.mine.activemq.util;

import java.util.Arrays;
import java.util.List;

public class LeetcodeUtil {
    public static List<List<String>> groupAnagrams(String[] strs) {
        String[] newStrs = new String[strs.length];
        for(int i=0;i<strs.length;i++){
            char[] chs = strs[i].toCharArray();
            Arrays.sort(chs);
            newStrs[i] = new String(chs);
        }

        return null;
    }

    public static void main(String[] args) {
        groupAnagrams(new String[]{"tea","eat","tee"});
    }
}
