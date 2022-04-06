package com.example.webcrawler;


import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CSVLinkWriter {

    private CSVWriter csvWriter;

    private final String FILE_FORMAT_CSV = ".csv";
    private final String FILE_PATH_DELIMITER = "\\";
    private final String HEADER_URL = "URL";
    private final String HEADER_TOTAL_MATCHES = "Total matches";


    public void createFile(final String path, final String[] keywords, final List<Link> links) throws IOException {
        File file = new File(path + FILE_PATH_DELIMITER + System.currentTimeMillis() + FILE_FORMAT_CSV);
        csvWriter = new CSVWriter(new FileWriter(file, StandardCharsets.UTF_8));
        csvWriter.writeNext(createHeaders(keywords));
        links.forEach(l -> csvWriter.writeNext(convertLinkInfo(l)));
        csvWriter.close();
    }

    private String[] convertLinkInfo(final Link link) {
        return Stream.concat(Stream.concat(Arrays.stream(new String[]{String.valueOf(link.getTotalMatches())}),
                        link.getKeywordsMatches().stream().map(String::valueOf)), Arrays.stream(new String[]{link.getURL()}))
                .toArray(String[]::new);
    }

    private String[] createHeaders(String[] keywords) {
        return Stream.concat(Stream.concat(Arrays.stream(new String[]{HEADER_TOTAL_MATCHES}),
                        Arrays.stream(keywords)), Arrays.stream(new String[]{HEADER_URL}))
                .toArray(String[]::new);
    }

}
