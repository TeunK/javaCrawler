package models;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Teun on 12-12-2017.
 */
public class SiteMap {
    private final Map<WebNode, ChildNodes> pages = new ConcurrentHashMap<>();

    public void addNode(WebNode node, List<WebNode> childNodes) {
        pages.put(node, new ChildNodes(node, childNodes));
    }

    public Map<WebNode, ChildNodes> getSiteMap() {
        return pages;
    }
}
