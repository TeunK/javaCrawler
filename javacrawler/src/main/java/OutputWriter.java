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

    public void writeSiteMapToConsole(SiteMap siteMap) throws FileNotFoundException, UnsupportedEncodingException {
        siteMap.getSiteMap().forEach((webNode, childNodes) -> {
            System.out.println(webNode.getRootUrl().toString());
            childNodes.getNodes().stream()
                    .forEach(childNode -> System.out.println("\t"+childNode.toString()));
            System.out.println();
        });
    }
}
