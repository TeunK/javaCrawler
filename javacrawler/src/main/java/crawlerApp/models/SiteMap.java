package crawlerApp.models;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by Teun on 12-12-2017.
 */
public class SiteMap {
    private static final Logger logger = Logger.getLogger(SiteMap.class.getName());

    private final Map<WebNode, ChildNodes> pages = new ConcurrentHashMap<>();

    public void addNode(WebNode node, List<WebNode> childNodes) {
        pages.put(node, new ChildNodes(node, childNodes));
    }

    public Map<WebNode, ChildNodes> getSiteMap() {
        return pages;
    }

    public String toJsonString() {

        Map<WebNode, Integer> webnodeIdMap = new HashMap<WebNode, Integer>();
        List<Edge> edges = new ArrayList<>();

        int index = 0;
        // Create store of Webnode -> Index mapping
        for (WebNode webNode : pages.keySet()) {
            webnodeIdMap.put(webNode, index++);
        }

        // Map each edge from parentWebNode -> childWebNode to their assigned Index
        for (Map.Entry<WebNode,ChildNodes> parentChildRelationshipSet : pages.entrySet()) {
            WebNode parentNode = parentChildRelationshipSet.getKey();
            Set<WebNode> childNodes = parentChildRelationshipSet.getValue().getNodes();

            if (webnodeIdMap.containsKey(parentNode)){
                int parentWebNodeId = webnodeIdMap.get(parentNode);
                for (WebNode childNode : childNodes) {
                    if (webnodeIdMap.containsKey(childNode)) {
                        edges.add(new Edge(parentWebNodeId, webnodeIdMap.get(childNode)));
                    } else {
                        logger.info("Could not find index for node " + childNode.getRootUrl());
                    }
                }
            } else {
                logger.info("Could not find index for node " + parentNode.getRootUrl());
            }
        }

        StringBuilder sb = new StringBuilder();

        sb.append("{ nodes: [");
        pages.keySet().forEach(parentWebNode -> sb.append("{name:\""+ parentWebNode.toString() +"\" },"));
        sb.append("], edges: [");
        edges.forEach(edge -> sb.append("{source:"+edge.getParent()+",target:"+edge.getChild()+"},"));
        sb.append("]}");

        return sb.toString();
    }

    private class Edge {
        private int parent;
        private int child;

        public Edge(int parent, int child) {
            this.parent = parent;
            this.child = child;
        }

        public int getParent() {
            return parent;
        }

        public int getChild() {
            return child;
        }
    }
}
