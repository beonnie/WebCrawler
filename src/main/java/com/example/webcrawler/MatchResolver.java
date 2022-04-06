package com.example.webcrawler;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatchResolver {

    public List<Integer> resolveKeywordsMatches(String[] keywords, String pageText) {
        List<Integer> matches = new ArrayList<>();
        Arrays.stream(keywords).forEach(e -> matches.add(StringUtils.countMatches(pageText, e)));
        return matches;
    }

}
