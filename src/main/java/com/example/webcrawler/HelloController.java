package com.example.webcrawler;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HelloController implements Initializable {

    @FXML
    private TextField startUrl;
    @FXML
    private FlowPane primaryFlow;
    @FXML
    private TextField keywords;
    @FXML
    private TextField crawlDepth;
    @FXML
    private TextField linksLimit;


    private WebCrawler webCrawler;
    private ObservableList<Link> links;
    private Stage primaryStage;
    private Thread thread;
    private CSVLinkWriter csvFileWriter;


    @FXML
    protected void onStartCrawlBtnClick() {
        links.clear();
        initCrawler();

        thread = new Thread(webCrawler);
        thread.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        links = FXCollections.observableArrayList();
        ListView<Link> linksListView = new ListView<>(links);
        linksListView.setPrefHeight(200);
        primaryFlow.getChildren().add(linksListView);
        csvFileWriter = new CSVLinkWriter();
    }

    @FXML
    public void onExportCSVBtnClick() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(primaryStage);
        csvFileWriter.createFile(dir.getAbsolutePath(), convertKeywordsStringToArray(keywords.getText()), links);
    }

    @FXML
    public void onExportTop10CSVBtnClick() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(primaryStage);
        List<Link> links = this.links.stream().sorted().collect(Collectors.toList()).subList(0, 10);
        csvFileWriter.createFile(dir.getAbsolutePath(), convertKeywordsStringToArray(keywords.getText()), links);
    }

    @FXML
    public void onStopCrawlBtnClick() {
        thread.stop();
    }

    private void initCrawler() {
        webCrawler = new WebCrawler(startUrl.getText());
        webCrawler.setDisplayedLinks(links);
        webCrawler.setKeywords(convertKeywordsStringToArray(keywords.getText()));
        setCrawlDepth();
        setLinksLimit();
    }

    private void setCrawlDepth() {
        try {
            webCrawler.setCrawlDepth(Integer.parseInt(crawlDepth.getText()));
        } catch (Exception e) {
            webCrawler.setCrawlDepth(Constants.DEFAULT_CRAWL_DEPTH);
        }
    }

    private void setLinksLimit() {
        try {
            webCrawler.setLinksLimit(Integer.parseInt(linksLimit.getText()));
        } catch (Exception e) {
            webCrawler.setLinksLimit(Constants.DEFAULT_LINKS_LIMIT);
        }
    }

    private String[] convertKeywordsStringToArray(String keywords) {
        return keywords.replace(StringUtils.SPACE, StringUtils.EMPTY).split(Constants.KEYWORDS_DELIMITER);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void onCloseBtnClick() {
        Platform.exit();
    }
}