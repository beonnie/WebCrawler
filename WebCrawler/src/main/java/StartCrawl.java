public class StartCrawl {
    public static void main(String[] args){
        String URL = "https://github.com";
        Crawler crawler = new Crawler(URL, 10000, 8 , new String[]{"project","build","Code"});
    }
}
