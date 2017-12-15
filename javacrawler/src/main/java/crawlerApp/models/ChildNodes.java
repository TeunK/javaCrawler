package crawlerApp.models;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Teun on 12-12-2017.
 */
public class ChildNodes {
    private final WebNode parentNode;
    private final Set<WebNode> childNodes = ConcurrentHashMap.newKeySet();

    public ChildNodes(WebNode parentNode, List<WebNode> childNodes) {
        this.parentNode = parentNode;
        childNodes.forEach(this.childNodes::add);
    }

    public Set<WebNode> getNodes(){
        return childNodes;
    }
}
