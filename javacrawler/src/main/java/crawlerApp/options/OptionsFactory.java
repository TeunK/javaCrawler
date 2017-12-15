package crawlerApp.options;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Created by Teun on 12-12-2017.
 */
public class OptionsFactory {
    public Options getDefaultOptions(){
        Options options = new Options();
        options.addOption(getDefaultCrawlersCountOption());
        options.addOption(getDefaultUrlOption());
        options.addOption(getDefaultDepthOption());
        options.addOption(getDefaultOutputFileOption());
        return options;
    }

    private Option getDefaultCrawlersCountOption() {
        return Option.builder()
                .argName(Constants.ARGNAME_CRAWLERS)
                .desc(Constants.DESC_CRAWLERS)
                .longOpt(Constants.CRAWLER_COUNT)
                .hasArg(true)
                .build();
    }

    private Option getDefaultUrlOption() {
        return Option.builder()
                .argName(Constants.ARGNAME_URL)
                .desc(Constants.DESC_URL)
                .longOpt(Constants.CRAWLING_URL)
                .hasArg(true)
                .build();
    }

    private Option getDefaultDepthOption() {
        return Option.builder()
                .argName(Constants.ARGNAME_DEPTH)
                .desc(Constants.DESC_DEPTH)
                .longOpt(Constants.CRAWLING_DEPTH)
                .hasArg(true)
                .build();
    }

    private Option getDefaultOutputFileOption() {
        return Option.builder()
                .argName(Constants.ARGNAME_OUTPUT_FILE_PATH)
                .desc(Constants.DESC_OUTPUT_FILE_PATH)
                .longOpt(Constants.OUTPUT_FILE_PATH)
                .hasArg(true)
                .build();
    }
}