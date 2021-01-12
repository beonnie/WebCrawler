import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Crawler {
    private final Set<URL> crawledLinks;
    private int crawlDepth;
    private int pagesLimit;
    private ConcurrentHashMap<String,Integer[]> top10ByTotal;
    private String[] terms;

    Comparator<Map.Entry<String,Integer[]>> comparatorObject = (e1,e2)
            -> e1.getValue()[e2.getValue().length-1].compareTo(e2.getValue()[e1.getValue().length-1]);


    /**
     *
     * @param startURL - address from which crawling will start
     * @param pagesLimit - max value of the pages which will be crawled
     * @param crawlDepth - value of the clicks on the links relative start url
     * @param terms - keywords a number of which will calculate on each page
     * In constructor input data checked for correctness
     * and in case incorrect input exception generate and print according message.
     * The rest methods used in constructor and if they generate exceptions
     * exceptions will be processing in constructor
     */
    public Crawler(String startURL, int pagesLimit , int crawlDepth , String[] terms) {
        this.crawledLinks = new HashSet<>();
        top10ByTotal = new ConcurrentHashMap<>();
        try {
            if(terms.length == 0) throw new IOException(); else this.terms = terms;
            if(crawlDepth < 1) throw new IOException(); else this.crawlDepth = crawlDepth;
            if(pagesLimit < 1) throw new IOException(); else this.pagesLimit = pagesLimit;



            crawl(getSet(new URL(startURL)));


        } catch (MalformedURLException exception) {
            System.out.println("Incorrect start URL");
        }catch (IOException exception){
            System.out.println("Incorrect input data");
        }
    }

    private static Set<URL> getSet(final URL startURL) {
        Set<URL> startURLS = new HashSet<>();
        startURLS.add(startURL);
        return startURLS;
    }

    /**
     * Here from transmitted set of URLs deleted urls from
     * class variable links which store urls that already crawled
     * on the previous step.
     * With the help of the Jsoup library retrieve all links and
     * all text from the current page.
     * In extracted text calculated number of keywords with the help
     * of the Apache Common Language library's method and this info recorded
     * to the CSV file.
     * On the next step retrieved URLs will be processed in the same way
     * when the Crawl method will call itself with retrieved URls as argument.
     * Also in this method along with each viewed address concurrent HashMap which store
     * top 10 links by hits checked and if it necessary rerecorded so this hashmap updated on each step
     */
    private void crawl(Set<URL> URLS) {
        if(crawlDepth--<1) return;
        URLS.removeAll(this.crawledLinks);
        if (!URLS.isEmpty()) {
            Set<URL> newURLS = new HashSet<>();
            try {
                this.crawledLinks.addAll(URLS);
                for (final URL url : URLS) {
                    final Document document = Jsoup.connect(url.toString())
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36 OPR/70.0.3728.106")
                    .referrer("http://www.google.com").get();
                    final Elements linksOnPage = document.select("a[href]");
                    final Elements elements = document.getAllElements();
                    int[] numberOfHits = new int[terms.length + 1];
                    for (final Element page : linksOnPage) {
                        final String urlText = page.attr("abs:href").trim();
                        final URL discoveredURL = new URL(urlText);
                        newURLS.add(discoveredURL);
                    }

                    if(pagesLimit--<1) return;

                    for(Element element : elements){
                        String TextOfThePage = "";
                        for(TextNode node : element.textNodes()){
                            TextOfThePage = TextOfThePage + node;
                        }

                        for(int i=0;i< terms.length;i++){
                            numberOfHits[i] = numberOfHits[i] + StringUtils.countMatches(TextOfThePage,terms[i]);
                            numberOfHits[terms.length] = numberOfHits[terms.length] + StringUtils.countMatches(TextOfThePage,terms[i]);
                        }
                    }


                    checkTop10ByTotalHits(url,numberOfHits);
                    writeToCsvFile(url, numberOfHits);
                    writeTop10();

                    System.out.println(pagesLimit+" "+url+ arrayToString(numberOfHits));

                }
            } catch (Exception exception) {}
            crawl(newURLS);
        }
    }


    /**
     *  Records transmitted URL address and number of the
     *  hits to a CSV file that located in project directory
     */
    private void writeToCsvFile(URL url, int[] numberOfHits){
        try {
            CSVWriter writer = new CSVWriter(new FileWriter("stat.csv",true));
            writer.writeNext(new String[]{url+arrayToString(numberOfHits)});
            writer.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    /**
     *  Records URL address and number of the
     *  hits from the hashmap that contains links
     *  with the highest number of matches
     *  to a CSV file that located in project directory
     */
    private void writeTop10(){

        LinkedHashMap<String,Integer[]> mapSort = top10ByTotal.entrySet().stream().sorted(comparatorObject)
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(e1,e2)->e2,LinkedHashMap::new));
        CSVWriter csvWriter = null;
        try {
            csvWriter = new CSVWriter(new FileWriter("top10byTotalHits.csv",false));
            for(Map.Entry<String,Integer[]> entry : mapSort.entrySet()){
                final String[] str = {"\t"};
                Arrays.stream(entry.getValue()).forEach(value -> str[0] = str[0] +value+" ");
                csvWriter.writeNext(new String[]{entry.getKey()+str[0]});
            }
            csvWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


    /**
     * This method compares the number of matches between the passed link and all links in the hash map.
     * The least number of matches for links in the hash map is checked which is greater than the number of
     * matches in the passed link and if such a link exists in the hash map it is
     * removed and the passed link is added instead link that has been deleted
     */
    private void checkTop10ByTotalHits(URL url, int[] arr){
        if(top10ByTotal.size() == 0 ) top10ByTotal.put(url.toString(),IntStream.of(arr).boxed().toArray(Integer []::new));else {
            if(top10ByTotal.size()<10) {
                for(Map.Entry<String,Integer[]> entry : top10ByTotal.entrySet()){

                    if(!url.toString().split("#")[0].contains(entry.getKey().split("#")[0]))
                        top10ByTotal.put(url.toString(),IntStream.of(arr).boxed().toArray(Integer []::new));else  break;

                }
            }else {
                LinkedHashMap<String,Integer[]> mapSort = top10ByTotal.entrySet().stream().sorted(comparatorObject)
                        .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(e1,e2)->e2,LinkedHashMap::new));
                int value = arr[arr.length-1];

                for(Map.Entry<String,Integer[]> entry : mapSort.entrySet()){

                    if(url.toString().split("#")[0].contains(entry.getKey().split("#")[0])) break;

                    if(arr[arr.length-1] > entry.getValue()[entry.getValue().length-1]){
                        top10ByTotal.remove(entry.getKey());
                        top10ByTotal.put(url.toString(),IntStream.of(arr).boxed().toArray(Integer[]::new));
                    }

                }



            }
        }
    }


    /**
     * Return more convenient string representation
     * of the int array for the console output
     */
    private String arrayToString(int[] array){
        final String[] str = {"\t"};
        Arrays.stream(array).forEach(arr -> str[0] = str[0] + " " + arr);
        return str[0];
    }

}
