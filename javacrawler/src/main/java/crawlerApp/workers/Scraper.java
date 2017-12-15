package crawlerApp.workers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by Teun on 12-12-2017.
 */
public class Scraper {
    public Elements scrape(String htmlContentBody) {
        Document doc = Jsoup.parse(htmlContentBody);
        Elements links = doc.select("a[href]");
        return links;
    }
}
