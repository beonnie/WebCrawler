package com.example.webcrawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class LinkProvider {

    private final String LINK_CSS_QUERY = "a[href]";
    private final String LINK_ATTRIBUTE_KEY = "abs:href";

    public List<String> extractURLs(final Document document) {
        List<String> urls = new ArrayList<>();
        Elements select = document.select(LINK_CSS_QUERY);
        for (final Element element : select) {
            final String url = element.attr(LINK_ATTRIBUTE_KEY).trim();
            urls.add(url);
        }
        return urls;
    }
}
