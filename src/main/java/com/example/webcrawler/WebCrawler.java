package com.example.webcrawler;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebCrawler implements Runnable {

    private ObservableList<Link> displayedLinks;
    private final LinkProvider linkProvider;
    private String[] keywords;
    private int crawlDepth;
    private int linksLimit;
    private final MatchResolver matchResolver;
    private final List<String> URLs;
    private final HtmlProvider htmlProvider;

    public WebCrawler(String startUrl) {
        htmlProvider = new HtmlProvider();
        linkProvider = new LinkProvider();
        matchResolver = new MatchResolver();
        URLs = new ArrayList<>();
        URLs.add(startUrl);
    }

    @Override
    public void run() {
        try {
            startCrawl();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startCrawl() throws IOException {
        int currentDepthLevelCapacity = 1;
        while (crawlDepth-- > 0) {
            for (int i = 0; i < currentDepthLevelCapacity; i++) {
                if (displayedLinks.size() == linksLimit - 1) return;
                final String currentUrl = URLs.get(i);
                Document document = getDocument(currentUrl);
                URLs.addAll(linkProvider.extractURLs(document));
                Platform.runLater(() -> displayedLinks.add(createLink(currentUrl, matchResolver.resolveKeywordsMatches(keywords, htmlProvider.getHtml(document)))));
            }
            replaceLinks(currentDepthLevelCapacity);
            currentDepthLevelCapacity = URLs.size();
        }
    }

    private Link createLink(String URL, List<Integer> keywordsMatches) {
        Link link = new Link();
        link.setKeywordsMatches(keywordsMatches);
        link.setURL(URL);
        link.setTotalMatches(keywordsMatches.stream().mapToInt(Integer::intValue).sum());
        return link;
    }

    private Document getDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(Constants.USER_AGENT)
                .followRedirects(Boolean.TRUE)
                .get();
    }

    private void replaceLinks(int currentDepthLevelCapacity) {
        URLs.removeAll(URLs.subList(0, currentDepthLevelCapacity));
    }

    public void setDisplayedLinks(ObservableList<Link> displayedLinks) {
        this.displayedLinks = displayedLinks;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    public void setCrawlDepth(int crawlDepth) {
        this.crawlDepth = crawlDepth;
    }

    public void setLinksLimit(int linksLimit) {
        this.linksLimit = linksLimit;
    }
}
