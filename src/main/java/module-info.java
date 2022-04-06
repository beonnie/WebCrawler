module com.example.webcrawler {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;
    requires com.opencsv;
    requires org.apache.commons.lang3;


    opens com.example.webcrawler to javafx.fxml;
    exports com.example.webcrawler;
}