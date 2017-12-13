package options;

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
    private final int maxDepth;
    private final String outputFilePath;

    public StartupParameters(String url, int workerCount, int maxDepth, String outputFilePath) throws MalformedURLException {
        this.url = new URL((url.endsWith("/")) ? url : String.format("%s/", url));
        this.workerCount = workerCount;
        this.maxDepth = maxDepth;
        this.outputFilePath = outputFilePath;
    }

    public URL getUrl() {
        return url;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public boolean isValid() {
        boolean isValid = workerCount >= 1 && workerCount <= 20
                && maxDepth >= 0 && maxDepth <= 20;

        if (!isValid)
            logger.warning("Parameters provided were invalid");

        return isValid;
    }
}
