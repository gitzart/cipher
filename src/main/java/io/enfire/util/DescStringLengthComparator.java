package io.enfire.util;

import java.util.Comparator;

/**
 * This class compares lengths of strings in descending order.
 */
public class DescStringLengthComparator implements Comparator<String> {
    @Override
    public int compare(String s1, String s2) {
        return s2.length() - s1.length();
    }
}