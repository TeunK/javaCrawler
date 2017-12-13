import com.github.tomakehurst.wiremock.junit.WireMockRule;
import models.ChildNodes;
import models.SiteMap;
import models.WebNode;
import options.StartupParameters;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import workers.Crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by Teun on 13-12-2017.
 */
public class CrawlerTest {
    private BlockingQueue<WebNode> workerQueue;
    private Set<URL> visitedUrls;
    private SiteMap siteMap;
    private Crawler worker;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8080));
    private static String localhost = "http://localhost:8080";


    @Before
    public void setUp() throws MalformedURLException {
        workerQueue = new LinkedBlockingQueue<>();
        visitedUrls = Collections.synchronizedSet(new HashSet<>());
        siteMap = new SiteMap();
    }

    @Test
    public void receivedValidOptionsTest() throws MalformedURLException {
        StartupParameters parameters = new StartupParameters("http://somewebsite.com", 1, 100, "result.txt");
        assertThat(parameters.isValid(), is(true));
    }

    @Test
    public void visitUrlSuccessfullyTest() throws MalformedURLException {
        WebPage webpage = new WebPage(localhost, "/");

        stubUrlWithWebpage(webpage.getPageName(), "Content");
        worker = new Crawler(JerseyClientBuilder.createClient(), workerQueue, visitedUrls, siteMap, webpage.getUrl());
        workerQueue.add(webpage.getWebNode());

        worker.run();

        verify(1, getRequestedFor(urlPathEqualTo(webpage.getPageName())));
        assertThat(siteMap.getSiteMap().size(), is(1));
        assertThat(siteMap.getSiteMap().get(webpage.getWebNode()), notNullValue());
    }

    @Test
    public void visitAllInternalSingleUrlsTest() throws MalformedURLException {
        WebPage page1 = new WebPage(localhost, "/");
        WebPage page2 = new WebPage(localhost, "/page");
        WebPage page3 = new WebPage(localhost, "/bla");

        stubUrlWithWebpage(page1.getPageName(), "<a href='/page'>page</a>");
        stubUrlWithWebpage(page2.getPageName(), "<a href='/bla'>link to bla</a>");
        stubUrlWithWebpage(page3.getPageName(), "blapage");

        workerQueue.add(page1.getWebNode());

        worker = new Crawler(JerseyClientBuilder.createClient(), workerQueue, visitedUrls, siteMap, page1.getUrl());
        worker.run();

        verify(1, getRequestedFor(urlPathEqualTo(page1.getPageName())));
        verify(1, getRequestedFor(urlPathEqualTo(page2.getPageName())));
        verify(1, getRequestedFor(urlPathEqualTo(page3.getPageName())));

        assertThat(workerQueue.size(), is(0));
        assertThat(siteMap.getSiteMap().size(), is(3));

        ChildNodes childNodes = siteMap.getSiteMap().get(page1.getWebNode());
        assertThat(childNodes.getNodes().size(), is(1));
        assertThat(childNodes.getNodes().contains(page2.getWebNode()), is(true));

        ChildNodes childNodes2 = siteMap.getSiteMap().get(page2.getWebNode());
        assertThat(childNodes2.getNodes().size(), is(1));
        assertThat(childNodes2.getNodes().contains(page3.getWebNode()), is(true));
    }

    @Test
    public void visitAllInternalMultipleUrlsTest() throws MalformedURLException {
        WebPage page1 = new WebPage(localhost, "/1");
        WebPage page2 = new WebPage(localhost, "/2");
        WebPage page3 = new WebPage(localhost, "/3");
        WebPage page4 = new WebPage(localhost, "/4");
        WebPage page5 = new WebPage(localhost, "/5");
        WebPage page6 = new WebPage(localhost, "/6");
        WebPage page7 = new WebPage(localhost, "/7");
        WebPage page8 = new WebPage(localhost, "/8");

        stubUrlWithWebpage(page3.getPageName(), "<a href='/5'>click here</a>");
        stubUrlWithWebpage(page2.getPageName(), "<a href='/4'>click here</a>");
        stubUrlWithWebpage(page1.getPageName(), "<a href='/2'>click here</a><a href='/3'>click here</a>");
        stubUrlWithWebpage(page4.getPageName(), "final node");
        stubUrlWithWebpage(page5.getPageName(), "<a href='/6'>click here</a><a href='/7'>click here</a><a href='/8'>click here</a>");
        stubUrlWithWebpage(page6.getPageName(), "final node");
        stubUrlWithWebpage(page7.getPageName(), "final node");
        stubUrlWithWebpage(page8.getPageName(), "final node");

        workerQueue.add(page1.getWebNode());

        worker = new Crawler(JerseyClientBuilder.createClient(), workerQueue, visitedUrls, siteMap, page1.getUrl());
        worker.run();

        verify(1, getRequestedFor(urlPathEqualTo(page1.getPageName())));
        verify(1, getRequestedFor(urlPathEqualTo(page2.getPageName())));
        verify(1, getRequestedFor(urlPathEqualTo(page3.getPageName())));
        verify(1, getRequestedFor(urlPathEqualTo(page4.getPageName())));
        verify(1, getRequestedFor(urlPathEqualTo(page5.getPageName())));
        verify(1, getRequestedFor(urlPathEqualTo(page6.getPageName())));
        verify(1, getRequestedFor(urlPathEqualTo(page7.getPageName())));
        verify(1, getRequestedFor(urlPathEqualTo(page8.getPageName())));

        assertThat(workerQueue.size(), is(0));
        assertThat(siteMap.getSiteMap().size(), is(8));

        ChildNodes childNodes1 = siteMap.getSiteMap().get(page1.getWebNode());
        assertThat(childNodes1.getNodes().size(), is(2));
        assertThat(childNodes1.getNodes().contains(page2.getWebNode()), is(true));
        assertThat(childNodes1.getNodes().contains(page3.getWebNode()), is(true));

        ChildNodes childNodes2 = siteMap.getSiteMap().get(page2.getWebNode());
        assertThat(childNodes2.getNodes().size(), is(1));
        assertThat(childNodes2.getNodes().contains(page4.getWebNode()), is(true));

        ChildNodes childNodes3 = siteMap.getSiteMap().get(page3.getWebNode());
        assertThat(childNodes3.getNodes().size(), is(1));
        assertThat(childNodes3.getNodes().contains(page5.getWebNode()), is(true));

        ChildNodes childNodes4 = siteMap.getSiteMap().get(page4.getWebNode());
        assertThat(childNodes4.getNodes().size(), is(0));

        ChildNodes childNodes5 = siteMap.getSiteMap().get(page5.getWebNode());
        assertThat(childNodes5.getNodes().size(), is(3));
        assertThat(childNodes5.getNodes().contains(page6.getWebNode()), is(true));
        assertThat(childNodes5.getNodes().contains(page7.getWebNode()), is(true));
        assertThat(childNodes5.getNodes().contains(page8.getWebNode()), is(true));

        ChildNodes childNodes6 = siteMap.getSiteMap().get(page6.getWebNode());
        assertThat(childNodes6.getNodes().size(), is(0));

        ChildNodes childNodes7 = siteMap.getSiteMap().get(page7.getWebNode());
        assertThat(childNodes7.getNodes().size(), is(0));

        ChildNodes childNodes8 = siteMap.getSiteMap().get(page8.getWebNode());
        assertThat(childNodes8.getNodes().size(), is(0));
    }

    @Test
    public void doesNotVisitExternalDomains() throws MalformedURLException {
        WebPage page1 = new WebPage(localhost, "/");

        stubUrlWithWebpage(page1.getPageName(), "<a href='http://fakethirdpartyhost.com'>click here</a>");

        workerQueue.add(page1.getWebNode());

        worker = new Crawler(JerseyClientBuilder.createClient(), workerQueue, visitedUrls, siteMap, page1.getUrl());
        worker.run();

        verify(1, getRequestedFor(urlPathEqualTo(page1.getPageName())));

        assertThat(workerQueue.size(), is(0));
        assertThat(siteMap.getSiteMap().size(), is(1));

        ChildNodes childNodes1 = siteMap.getSiteMap().get(page1.getWebNode());
        assertThat(childNodes1.getNodes().size(), is(0));
    }

    @Test
    public void doesNotLoopInfinitelyTest() throws MalformedURLException {
        WebPage page1 = new WebPage(localhost, "/1");
        WebPage page2 = new WebPage(localhost, "/2");

        stubUrlWithWebpage(page1.getPageName(), "<a href='/2'>click here</a>");
        stubUrlWithWebpage(page2.getPageName(), "<a href='/1'>click here</a>");

        workerQueue.add(page1.getWebNode());

        worker = new Crawler(JerseyClientBuilder.createClient(), workerQueue, visitedUrls, siteMap, page1.getUrl());
        worker.run();

        verify(1, getRequestedFor(urlPathEqualTo(page1.getPageName())));
        verify(1, getRequestedFor(urlPathEqualTo(page2.getPageName())));

        assertThat(workerQueue.size(), is(0));
        assertThat(siteMap.getSiteMap().size(), is(2));

        ChildNodes childNodes1 = siteMap.getSiteMap().get(page1.getWebNode());
        assertThat(childNodes1.getNodes().size(), is(1));
        assertThat(childNodes1.getNodes().contains(page2.getWebNode()), is(true));

        ChildNodes childNodes2 = siteMap.getSiteMap().get(page2.getWebNode());
        assertThat(childNodes2.getNodes().size(), is(1));
        assertThat(childNodes2.getNodes().contains(page1.getWebNode()), is(true));
    }

    private void stubUrlWithWebpage(String url, String contentBody) {
        stubFor(get(urlEqualTo(url)).willReturn(aResponse().withBody(contentBody)));
    }

    private class WebPage {
        private final String pageName;
        private final String fullName;
        private final URL url;
        private final WebNode webNode;

        WebPage(String domain, String name) throws MalformedURLException {
            this.pageName = name;
            this.fullName = domain+name;
            this.url = new URL(this.fullName);
            this.webNode = new WebNode(this.url);
        }

        String getPageName() {
            return pageName;
        }

        URL getUrl() {
            return url;
        }

        WebNode getWebNode() {
            return webNode;
        }
    }
}
