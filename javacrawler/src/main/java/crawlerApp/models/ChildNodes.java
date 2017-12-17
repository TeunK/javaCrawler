package crawlerApp.models;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Teun on 12-12-2017.
 */
public class ChildNodes {
    private final Set<WebNode> childNodes = ConcurrentHashMap.newKeySet();

    ChildNodes(List<WebNode> childNodes) {
        this.childNodes.addAll(childNodes);
    }

    public Set<WebNode> getNodes(){
        return childNodes;
    }
}
