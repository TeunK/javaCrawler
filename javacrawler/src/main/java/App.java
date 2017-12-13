import exceptions.InvalidStartupParametersException;
import models.SiteMap;
import models.WebNode;
import org.apache.commons.cli.*;
import options.*;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import workers.CrawlerPool;

import javax.ws.rs.client.Client;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

/**
 * Created by Teun on 12-12-2017.
 */


public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {

        try {
            StartupParameters startupParameters = getStartupParameters(args);

            Client webClient = JerseyClientBuilder.createClient(new ClientConfig().property(ClientProperties.FOLLOW_REDIRECTS, true));

            BlockingQueue<WebNode> workerQueue = new LinkedBlockingDeque<>();

            CrawlerPool crawlerPool = new CrawlerPool(startupParameters, webClient, workerQueue);

            Future<SiteMap> siteMapFuture = crawlerPool.start();

            SiteMap siteMap = siteMapFuture.get();

            crawlerPool.stop();

            OutputWriter writer = new OutputWriter(startupParameters.getOutputFilePath());
            writer.writeToFile(siteMap);

        } catch (ParseException e) {
            logger.warning("Unable to parse startup arguments.");
        } catch (InvalidStartupParametersException e) {
            logger.warning(e.getMessage());
        } catch (MalformedURLException e) {
            logger.warning("Unable to parse the provided url.\nPlease ensure the url provided is of format: http://www.example.com");
        } catch (InterruptedException e) {
            logger.warning("The process was interrupted.");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            logger.warning("Failed to find specified file");
        } catch (UnsupportedEncodingException e) {
            logger.warning("Failed to encode content");
        }
    }

    private static StartupParameters getStartupParameters(String[] args) throws ParseException, InvalidStartupParametersException, MalformedURLException {
        OptionsFactory optionsFactory = new OptionsFactory();
        Options startupOptions = optionsFactory.getDefaultOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(startupOptions, args);

        // Set default values
        String url = Constants.DEFAULT_URL;
        String outputFilePath = Constants.DEFAULT_OUTPUT_FILE_PATH;
        int crawlerCount = Constants.DEFAULT_CRAWLER_COUNT;
        int maxDepth = Constants.DEFAULT_DEPTH;

        // Accept provided startup parameters
        if (cmd.hasOption(Constants.ARGNAME_URL))
            url = cmd.getOptionValue(Constants.ARGNAME_URL);

        if (cmd.hasOption(Constants.ARGNAME_CRAWLERS))
            crawlerCount = Integer.parseInt(cmd.getOptionValue(Constants.ARGNAME_CRAWLERS));

        if (cmd.hasOption(Constants.ARGNAME_DEPTH))
            maxDepth = Integer.parseInt(cmd.getOptionValue(Constants.ARGNAME_DEPTH));

        if (cmd.hasOption(Constants.ARGNAME_OUTPUT_FILE_PATH))
            outputFilePath = cmd.getOptionValue(cmd.getOptionValue(Constants.OUTPUT_FILE_PATH));

        StartupParameters startupParameters = new StartupParameters(url, crawlerCount, maxDepth, outputFilePath);

        if (!startupParameters.isValid())
            throw new InvalidStartupParametersException("The provided input parameters were invalid");

        return startupParameters;
    }
}
