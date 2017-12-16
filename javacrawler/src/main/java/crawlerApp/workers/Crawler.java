package crawlerApp.workers;

import com.sun.javaws.exceptions.InvalidArgumentException;
import crawlerApp.models.SiteMap;
import crawlerApp.models.WebNode;
import crawlerApp.options.Constants;
import org.jsoup.nodes.Element;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Teun on 12-12-2017.
 */
public class Crawler implements Runnable {
    private static final Logger logger = Logger.getLogger(Crawler.class.getName());
    private final Client webClient;
    private final BlockingQueue<WebNode> workerQueue;
    private final Set<URL> visitedUrls;
    private final SiteMap siteMap;
    private final URL rootUrl;
    private final long timeoutPeriod = Constants.MS_UNTIL_TIMEOUT;
    private final Scraper scraper = new Scraper();
    private final Predicate<WebNode> isInternalUrl;
    private final Pattern nonHtmlPattern = Pattern.compile(Constants.IMAGE_PATTERN);
    private final Predicate<WebNode> isHtmlPattern;

    public Crawler(Client webClient, BlockingQueue<WebNode> workerQueue, Set<URL> visitedUrls, SiteMap siteMap, URL rootUrl) {
        this.webClient = webClient;
        this.workerQueue = workerQueue;
        this.visitedUrls = visitedUrls;
        this.siteMap = siteMap;
        this.rootUrl = rootUrl;
        this.isInternalUrl = url -> url.getRootUrl().getHost().equalsIgnoreCase(rootUrl.getHost());
        this.isHtmlPattern = url -> !nonHtmlPattern.matcher(url.getRootUrl().getHost()).matches();
    }

    @Override
    public void run() {
        while (true) {
            try{
                WebNode node = workerQueue.poll(timeoutPeriod, TimeUnit.MILLISECONDS);

                if (node != null) {
                    if (!visitedUrls.contains(node.getRootUrl())) {
                        visitedUrls.add(node.getRootUrl());
                        crawlPage(node);
                    }
                } else {
                    break;
                }
            } catch (InterruptedException e) {
                if (workerQueue.isEmpty())
                    break;
            }
        }
    }

    private void crawlPage(WebNode node) {
        String currentUrl = node.getRootUrl().toString();

        try{
            WebTarget webTarget = webClient.target(currentUrl);
            Builder builder = webTarget.request(MediaType.TEXT_HTML);
            Response response = builder.get();
            String output = response.readEntity(String.class);

            List<WebNode> internalChildNodes = scraper.scrape(output).stream()
                    .map(link -> mapElementToWebNode(link))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(isInternalUrl)
                    .filter(isHtmlPattern)
                    .collect(Collectors.toList());
            siteMap.addNode(node, internalChildNodes);

            internalChildNodes.forEach(this::addToWorkerQueue);
        } catch (WebApplicationException e) {
            logger.warning("Failed to reach page\n" + e.getMessage());
        }  catch (IllegalArgumentException e) {
            logger.info("Excluding non-html page from results: " +node.getRootUrl());
        }  catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    private Optional<WebNode> mapElementToWebNode(Element link) {
        String href = link.attr("href");
        try {
            return Optional.of(new WebNode(new URL(rootUrl, href)));
        } catch (MalformedURLException e) {
            logger.warning("Failed to transform URL: "+href);
        }
        return Optional.empty();
    }

    private void addToWorkerQueue(WebNode webnode) {
        boolean isAlreadyVisited = visitedUrls.contains(webnode.getRootUrl());
        boolean isAlreadyQueued = workerQueue.contains(webnode);
        if (!isAlreadyVisited && !isAlreadyQueued)
            workerQueue.add(webnode);
    }
}
