package models;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Teun on 12-12-2017.
 */
public class ChildNodes {
    private final WebNode node;
    private final Set<WebNode> childNodes = ConcurrentHashMap.newKeySet();

    public ChildNodes(WebNode node, List<WebNode> childNodes) {
        this.node = node;
        childNodes.stream().forEach(this.childNodes::add);
    }

    public Set<WebNode> getNodes(){
        return childNodes;
    }
}
