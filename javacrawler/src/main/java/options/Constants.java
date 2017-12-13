package options;

/**
 * Created by Teun on 12-12-2017.
 */
public class Constants {
    public static String DEFAULT_URL = "http://www.monzo.com/";
    public static String DEFAULT_OUTPUT_FILE_PATH = "sitemap.txt";
    public static int DEFAULT_CRAWLER_COUNT = 5;
    public static int DEFAULT_DEPTH = 5;
    public static long MS_UNTIL_TIMEOUT = 100;

    public static final String CRAWLER_COUNT = "crawlers";
    public static final String CRAWLING_URL = "url";
    public static final String CRAWLING_DEPTH = "depth";
    public static final String OUTPUT_FILE_PATH = "output";

    public static final String ARGNAME_CRAWLERS = "worker_count";
    public static final String ARGNAME_URL = "crawling_url";
    public static final String ARGNAME_DEPTH = "crawling_depth";
    public static final String ARGNAME_OUTPUT_FILE_PATH = "output_file_path";

    public static final String DESC_CRAWLERS = "Number of concurrent crawlers";
    public static final String DESC_URL = "Crawler site target";
    public static final String DESC_DEPTH = "Max crawling depth";
    public static final String DESC_OUTPUT_FILE_PATH = "Output path for resulting sitemap";
}
