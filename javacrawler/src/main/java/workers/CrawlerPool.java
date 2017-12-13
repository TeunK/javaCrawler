package workers;

import models.SiteMap;
import models.WebNode;
import options.StartupParameters;

import javax.ws.rs.client.Client;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Created by Teun on 12-12-2017.
 */
public class CrawlerPool {
    private static final Logger logger = Logger.getLogger(CrawlerPool.class.getName());

    private final StartupParameters parameters;
    private final Client webClient;
    private final BlockingQueue<WebNode> workerQueue;
    private final Set<URL> visitedUrls = Collections.synchronizedSet(new HashSet<>());
    private final ExecutorService executorService;
    private final SiteMap siteMap = new SiteMap();

    public CrawlerPool(StartupParameters parameters, Client webClient, BlockingQueue<WebNode> workerQueue) {
        this.parameters = parameters;
        this.webClient = webClient;
        this.workerQueue = workerQueue;
        this.executorService = Executors.newFixedThreadPool(parameters.getWorkerCount());
        workerQueue.add(new WebNode(parameters.getUrl()));
    }

    public Future<SiteMap> start() {
        return CompletableFuture.supplyAsync(() -> {
            ArrayList<Future> futures = new ArrayList<>();

            for (int i=0; i < this.parameters.getWorkerCount(); i++) {
                futures.add(executorService.submit(
                    new Crawler(webClient, workerQueue, visitedUrls, siteMap, parameters.getUrl())
                ));
            }

            futures.stream().forEach(future -> {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    logger.severe("Workers were interrupted.\n"+e.getMessage());
                } catch (ExecutionException e) {
                    logger.severe(e.getMessage());
                }
            });

            return CrawlerPool.this.siteMap;
        });
    }

    public void stop() {
        executorService.shutdown();
    }


}
