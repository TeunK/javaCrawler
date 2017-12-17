package crawlerApp;

import crawlerApp.models.SiteMap;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Created by Teun on 12-12-2017.
 */
public class OutputWriter {
    private static final Logger logger = Logger.getLogger(OutputWriter.class.getName());
    private String rootDir;

    public OutputWriter() {
        this.rootDir = System.getProperty("user.dir");
    }

    public void writeSiteMapToConsole(SiteMap siteMap) throws FileNotFoundException, UnsupportedEncodingException {
        siteMap.getSiteMap().forEach((webNode, childNodes) -> {
            System.out.println(webNode.getRootUrl().toString());
            childNodes.getNodes().forEach(childNode -> System.out.println("\t"+childNode.toString()));
            System.out.println();
        });
    }

    public void writeSiteMapToFile(SiteMap sitemap, String fileName) {
        String filePath = rootDir+fileName;

        try{
            FileWriter fileWriter = new FileWriter(filePath);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            sitemap.getSiteMap().forEach(((webNode, childNodes) -> {
                printWriter.println(webNode.getRootUrl().toString());
                childNodes.getNodes().forEach(childNode -> printWriter.println("\t"+childNode.toString()));
                printWriter.println();
            }));

            logger.info("Written sitemap text output to "+filePath);
            System.out.println("Written sitemap text output to "+filePath);

            fileWriter.flush();
            printWriter.flush();
            printWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            logger.severe("Failed attempt to write to file "+filePath);
            e.printStackTrace();
        }
    }

    public void generateVisualWebGraph(SiteMap siteMap, String fileName) {
        String filePath = rootDir+fileName;
        InputStream graphHtmlTemplateStream = App.class.getClassLoader().getResourceAsStream("graphVisualiserTemplate.html");

        try {
            String htmlTemplateString = IOUtils.toString(graphHtmlTemplateStream, Charset.defaultCharset());


            String siteMapJsonFormat = siteMap.toJsonString();

            String finalWebGraphHtml = htmlTemplateString.replace("D3_VISUALISABLE_DATASET",siteMapJsonFormat);


            OutputStream outputStream = Files.newOutputStream(Paths.get(filePath));
            outputStream.write(finalWebGraphHtml.getBytes());
            outputStream.flush();
            outputStream.close();

            logger.info("Written sitemap visual output to "+filePath);
            System.out.println("Written sitemap visual output to "+filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
