import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class CrawlerTest {

    @Test
    public void incorrectURL(){
        Crawler crawler = new Crawler("ds4",3,56,new String[]{"ds","d57"});
    }

    @Test
    public void incorrectPageLimit(){
        Crawler crawler = new Crawler("https://en.wikipedia.org/wiki/Elon_Musk",-32,4,new String[]{"5g"});
    }

    @Test
    public void incorrectCrawlDepth(){
        Crawler crawler = new Crawler("https://en.wikipedia.org/wiki/Elon_Musk",5,0,new String[]{"Elon","Musk"});
    }

    @Test
    public void incorrectArrayOfTheTerms(){
        Crawler crawler = new Crawler("https://en.wikipedia.org/wiki/Elon_Musk",56,7,new String[]{});
    }

    @Test
    public void Method1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Crawler crawler = new Crawler("https://en.wikipedia.org/wiki/Elon_Musk",2,4,new String[]{"5g"});
        int[] aaa = new int[]{5,5,6};
        Method method = crawler.getClass().getDeclaredMethod("arrayToString", int[].class);
        method.setAccessible(true);
        System.out.println(method.invoke(crawler,aaa));
    }
}