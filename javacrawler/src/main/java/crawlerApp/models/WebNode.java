package crawlerApp.models;

import java.net.URL;

/**
 * Created by Teun on 12-12-2017.
 */
public class WebNode {
    private final URL rootUrl;

    public WebNode(URL rootUrl) {
        this.rootUrl = rootUrl;
    }

    public URL getRootUrl() {
        return rootUrl;
    }

    @Override
    public String toString() {
        return rootUrl.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebNode webNode = (WebNode) o;

        return getRootUrl().equals(webNode.getRootUrl());
    }

    @Override
    public int hashCode() {
        return getRootUrl().hashCode();
    }
}
