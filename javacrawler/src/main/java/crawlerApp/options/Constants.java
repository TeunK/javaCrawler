package crawlerApp.options;

/**
 * Created by Teun on 12-12-2017.
 */
public class Constants {
    public static String DEFAULT_URL = "https://monzo.com/";
    public static String DEFAULT_OUTPUT_FILE_PATH = "sitemap.txt";
    public static String DEFAULT_OUTPUT_HTML_PATH = "visualised.html";
    public static int DEFAULT_CRAWLER_COUNT = 25;
    public static long MS_UNTIL_TIMEOUT = 10000;
    public static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|jpeg|png|gif|bmp|JPG|JPEG|PNG|GIF|BMP))$)";

    public static final String CRAWLER_COUNT = "crawlers";
    public static final String CRAWLING_URL = "url";
    public static final String OUTPUT_HTML_PATH = "visual_output";
    public static final String OUTPUT_FILE_PATH = "txt_output";

    public static final String ARGNAME_CRAWLERS = "worker_count";
    public static final String ARGNAME_URL = "crawling_url";
    public static final String ARGNAME_OUTPUT_HTML_PATH = "output_html_path";
    public static final String ARGNAME_OUTPUT_FILE_PATH = "output_file_path";

    public static final String DESC_CRAWLERS = "Number of concurrent crawlers";
    public static final String DESC_URL = "Crawler site target";
    public static final String DESC_OUTPUT_HTML_PATH = "output path for resulting html visualisation";
    public static final String DESC_OUTPUT_FILE_PATH = "Output path for resulting sitemap";
}
