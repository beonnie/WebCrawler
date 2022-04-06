package com.example.webcrawler;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class Link implements Comparable<Link>{

    private String URL;
    private List<Integer> keywordsMatches;
    private int totalMatches;

    public int getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(int totalMatches) {
        this.totalMatches = totalMatches;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public List<Integer> getKeywordsMatches() {
        return keywordsMatches;
    }

    public void setKeywordsMatches(List<Integer> keywordsMatches) {
        this.keywordsMatches = keywordsMatches;
    }

    @Override
    public String toString() {
        return String.valueOf(totalMatches) + keywordsMatches + StringUtils.SPACE + StringUtils.SPACE + URL;
    }

    @Override
    public int compareTo(Link o) {
        return Integer.compare(o.getTotalMatches(), this.totalMatches);
    }
}
