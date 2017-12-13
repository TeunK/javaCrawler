import models.SiteMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * Created by Teun on 12-12-2017.
 */
public class OutputWriter {
    private String filePath;

    public OutputWriter(String filePath) {
        this.filePath = filePath;
    }

    public void writeToFile(SiteMap siteMap) throws FileNotFoundException, UnsupportedEncodingException {
        String outputFileDir = System.getProperty("user.dir")+"/"+filePath;

        // Create file if not yet exists
        File file = new File(outputFileDir);
        file.mkdirs();

        // Print each node along with its child nodes
        PrintWriter printWriter = new PrintWriter(outputFileDir, "UTF-8");
        siteMap.getSiteMap().forEach((webNode, childNodes) -> {
            printWriter.println(webNode.getRootUrl().toString());
            childNodes.getNodes().stream().forEach(childNode -> printWriter.println("\t"+childNode.toString()));
            printWriter.println("\n");
        });
    }
}
