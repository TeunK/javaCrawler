import com.github.tomakehurst.wiremock.junit.WireMockRule;
import crawlerApp.LogHandler;
import crawlerApp.exceptions.InvalidStartupParametersException;
import crawlerApp.models.ChildNodes;
import crawlerApp.models.SiteMap;
import crawlerApp.models.WebNode;
import crawlerApp.options.StartupParameters;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import crawlerApp.workers.Crawler;
import org.junit.rules.ExpectedException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

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

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private static String localhost = "http://localhost:8080";


    @Before
    public void setUp() throws MalformedURLException {
        LogHandler.setLogLevel(Level.WARNING);
        workerQueue = new LinkedBlockingQueue<>();
        visitedUrls = Collections.synchronizedSet(new HashSet<>());
        siteMap = new SiteMap();
    }

    @Test
    public void receivedValidOptionsTest() throws MalformedURLException, InvalidStartupParametersException {
        StartupParameters parameters = new StartupParameters("http://somewebsite.com", 1, "visual.html", "result.txt");
        assertThat(parameters.isValid(), is(true));
    }

    @Test
    public void invalidStartupParametersExceptionDueToZeroWorkersTest() throws MalformedURLException, InvalidStartupParametersException {
        exception.expect(InvalidStartupParametersException.class);
        StartupParameters parameters = new StartupParameters("http://somewebsite.com", 0, "visual.html", "result.txt");
    }

    @Test
    public void invalidStartupParametersExceptionDueToInvalidOutputFileTest() throws MalformedURLException, InvalidStartupParametersException {
        exception.expect(InvalidStartupParametersException.class);
        StartupParameters parameters = new StartupParameters("http://somewebsite.com", 1, "visual.html", "");
    }

    @Test
    public void invalidStartupParametersExceptionDueToInvalidVisualFileTest() throws MalformedURLException, InvalidStartupParametersException {
        exception.expect(InvalidStartupParametersException.class);
        StartupParameters parameters = new StartupParameters("http://somewebsite.com", 1, "", "result.txt");
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

        stubUrlWithWebpage(page1.getPageName(), "<a href='http://externaldomain.com'>click here</a>");

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

    @Test
    public void setsZeroChildrenFor404Pages() throws Exception {
        WebPage page = new WebPage(localhost, "/");
        WebPage page2 = new WebPage(localhost, "/2");

        stubUrlWithWebpage(page.getPageName(), "<a href='/2'>click here</a>");
        stubFor(
                get(urlEqualTo("/2"))
                .willReturn(aResponse().withStatus(404))
        );
        workerQueue.add(page.getWebNode());

        worker = new Crawler(JerseyClientBuilder.createClient(), workerQueue, visitedUrls, siteMap, page.getUrl());
        worker.run();

        verify(1, getRequestedFor(urlPathEqualTo("/")));
        verify(1, getRequestedFor(urlPathEqualTo("/2")));
        assertThat(workerQueue.size(), is(0));
        assertThat(siteMap.getSiteMap().size(), is(2));
        assertThat(siteMap.getSiteMap().get(page.getWebNode()).getNodes().size(), is(1));
        assertThat(siteMap.getSiteMap().get(page2.getWebNode()).getNodes().size(), is(0));
    }

    @Test
    public void doesNotUrlWithImageFileExtensionTest() throws Exception {
        WebPage page = new WebPage(localhost, "/");
        WebPage page2 = new WebPage(localhost, "/image1.jpg");
        WebPage page3 = new WebPage(localhost, "/image2.jpeg");
        WebPage page4 = new WebPage(localhost, "/image3.png");
        WebPage page5 = new WebPage(localhost, "/image4.gif");
        WebPage page6 = new WebPage(localhost, "/image5.bmp");

        stubUrlWithWebpage(page.getPageName(), "<a href='/image1.jpg'></a><a href='/image2.jpeg'></a><a href='/image3.png'></a><a href='/image4.gif'></a><a href='/image5.bmp'></a>");
        stubUrlWithWebpage(page2.getPageName(), "110010101101010101");
        stubUrlWithWebpage(page3.getPageName(), "110010101101010101");
        stubUrlWithWebpage(page4.getPageName(), "110010101101010101");
        stubUrlWithWebpage(page5.getPageName(), "110010101101010101");
        stubUrlWithWebpage(page6.getPageName(), "110010101101010101");

        workerQueue.add(page.getWebNode());
        worker = new Crawler(JerseyClientBuilder.createClient(), workerQueue, visitedUrls, siteMap, page.getUrl());
        worker.run();

        verify(1, getRequestedFor(urlPathEqualTo(page.getPageName())));
        verify(0, getRequestedFor(urlPathEqualTo(page2.getPageName())));
        verify(0, getRequestedFor(urlPathEqualTo(page3.getPageName())));
        verify(0, getRequestedFor(urlPathEqualTo(page4.getPageName())));
        verify(0, getRequestedFor(urlPathEqualTo(page5.getPageName())));
        verify(0, getRequestedFor(urlPathEqualTo(page6.getPageName())));
        assertThat(workerQueue.size(), is(0));
        assertThat(siteMap.getSiteMap().size(), is(1));
        assertThat(siteMap.getSiteMap().get(page.getWebNode()).getNodes().size(), is(0));
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
