package crawlerApp.options;

import crawlerApp.exceptions.InvalidStartupParametersException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created by Teun on 12-12-2017.
 */
public class StartupParameters {
    private static final Logger logger = Logger.getLogger(StartupParameters.class.getName());

    private final URL url;
    private final int workerCount;
    private final String outputFilePath;
    private final String outputHtmlPath;

    public StartupParameters(String url, int workerCount, String outputHtmlPath, String outputFilePath) throws MalformedURLException, InvalidStartupParametersException {
        this.url = new URL((url.endsWith("/")) ? url : String.format("%s/", url));
        this.workerCount = workerCount;
        this.outputFilePath = outputFilePath;
        this.outputHtmlPath = outputHtmlPath;

        if (!isValid()) throw new InvalidStartupParametersException("The provided input parameters were invalid");
    }

    public URL getUrl() {
        return url;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public String getOutputHtmlPath() {
        return outputHtmlPath;
    }

    public boolean isValid() {
        boolean isValid = true;

        if (workerCount < 1
                || outputHtmlPath == null
                || outputHtmlPath.isEmpty()
                || outputFilePath == null
                || outputFilePath.isEmpty()){
            logger.warning("Number of crawlers provided is invalid. Please select a range from 1 to 100 (inclusive)");
            isValid = false;
        }

        return isValid;
    }
}
