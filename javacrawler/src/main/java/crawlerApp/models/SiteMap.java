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
        pages.put(node, new ChildNodes(childNodes));
    }

    public Map<WebNode, ChildNodes> getSiteMap() {
        return pages;
    }

    public String toJsonString() {

        Map<WebNode, Integer> webNodeIdMap = new HashMap<>();
        List<Edge> edges = new ArrayList<>();

        int index = 0;
        // Create store of WebNode -> Index mapping
        for (WebNode webNode : pages.keySet()) {
            webNodeIdMap.put(webNode, index++);
        }

        // Map each edge from parentWebNode -> childWebNode to their assigned Index
        for (Map.Entry<WebNode,ChildNodes> parentChildRelationshipSet : pages.entrySet()) {
            WebNode parentNode = parentChildRelationshipSet.getKey();
            Set<WebNode> childNodes = parentChildRelationshipSet.getValue().getNodes();

            if (webNodeIdMap.containsKey(parentNode)){
                int parentWebNodeId = webNodeIdMap.get(parentNode);
                for (WebNode childNode : childNodes) {
                    if (webNodeIdMap.containsKey(childNode)) {
                        edges.add(new Edge(parentWebNodeId, webNodeIdMap.get(childNode)));
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
        pages.keySet().forEach(parentWebNode -> sb.append("{name:\"").append(parentWebNode.toString()).append("\" },"));
        sb.append("], edges: [");
        edges.forEach(edge -> sb.append("{source:").append(edge.getParent()).append(",target:").append(edge.getChild()).append("},"));
        sb.append("]}");

        return sb.toString();
    }

    private class Edge {
        private int parent;
        private int child;

        Edge(int parent, int child) {
            this.parent = parent;
            this.child = child;
        }

        int getParent() {
            return parent;
        }

        int getChild() {
            return child;
        }
    }
}
