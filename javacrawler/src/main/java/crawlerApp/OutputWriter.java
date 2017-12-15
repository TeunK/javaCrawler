package crawlerApp;

import crawlerApp.models.SiteMap;

import java.io.*;
import java.util.logging.Logger;

/**
 * Created by Teun on 12-12-2017.
 */
public class OutputWriter {
    private static final Logger logger = Logger.getLogger(OutputWriter.class.getName());
    private String filePath;

    public OutputWriter(String filePath) {
        this.filePath = System.getProperty("user.dir")+filePath;
    }

    public void writeSiteMapToConsole(SiteMap siteMap) throws FileNotFoundException, UnsupportedEncodingException {
        siteMap.getSiteMap().forEach((webNode, childNodes) -> {
            System.out.println(webNode.getRootUrl().toString());
            childNodes.getNodes().forEach(childNode -> System.out.println("\t"+childNode.toString()));
            System.out.println();
        });
    }

    public void writeSiteMapToFile(SiteMap sitemap) {
        try{
            PrintWriter printWriter = new PrintWriter(new FileWriter(filePath));
            sitemap.getSiteMap().forEach(((webNode, childNodes) -> {
                printWriter.println(webNode.getRootUrl().toString());
                childNodes.getNodes().forEach(childNode -> printWriter.println("\t"+childNode.toString()));
                printWriter.println();
            }));

            logger.info("Written sitemap to "+filePath);
        } catch (IOException e) {
            logger.severe("Failed attempt to write to file "+filePath);
            e.printStackTrace();
        }
    }
}
