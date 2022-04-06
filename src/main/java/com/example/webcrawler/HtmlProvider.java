package com.example.webcrawler;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class HtmlProvider {

    public String getHtml(final Document document) {
        StringBuilder pageText = new StringBuilder(StringUtils.EMPTY);
        final Elements elements = document.getAllElements();
        for (Element element : elements) {
            for (TextNode node : element.textNodes()) {
                pageText.append(node);
            }
        }
        return pageText.toString();
    }

}
