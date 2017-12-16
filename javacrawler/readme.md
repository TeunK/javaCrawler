# Web Crawler
Web Crawler by Teun Kokke

### Building the project
Navigate to the javacrawler folder containing the `\src` folder, and run:
`mvn package`

Finally, the runnable file can be executed by executing the following command:
`java -jar target/javacrawler-1.0-SNAPSHOT.jar`
This will by default crawl `https://monzo.com/` with a worker pool of `5 crawlers`.

**Additional flags**
Alternatively, the following flags are provided:`--url=<siteUrl>` and `--crawlers=<crawlerCount>`
`java -jar target/javacrawler-1.0-SNAPSHOT.jar --url=http://www.lazygirlrunning.com --crawlers=20`

### Considerations
- Not everything is implemented, such as the max-crawling-depth (which could be added as part of the WebNode class, to keep track of the depth at which this node was found. Note also that at this point, the parent node (url) will have to be considered in case the same node was found previously at a different depth).
- There are many many tests (and types of tests) that can be written, although I decided to cut it here and would like add more on request.
- Some feedback will be provided to the user, eg. when receiving bad input. This feedback could be more explicit to mention in more detail what exactly it is that went wrong.
- In this solution, I did not worry too much about logging events / errors. Typically one could setup a more sophisticated logging system by splitting different levels of logging priority into different streams to separate levels of concern. Eg. any low level importance logs can be written to a verbose log file, whereas high importance log levels (such as exceptions) can be written to a separate file or even be thrown into a messaging queue, for some kind of logging service to catch up.
- Not only will external pages not be crawled, they also won't be considered as child node for a given url.

### Technologies
- [Java 8](http://www.oracle.com/technetwork/java/javase/8-whats-new-2157071.html)
- [Jersey Client (Virtual web client for workers to operate on)](https://jersey.github.io/)
- [Apache Commons CLI (Handling CLI flags)](https://commons.apache.org/proper/commons-cli/)
- [JSoup (Scraping response bodies, used for stripping urls)](https://jsoup.org/)
- [Junit (Unit testing framework)](http://junit.org/junit5/)
- [Hamcrest (Personal preference to make testing functions more intuitive)](http://hamcrest.org/JavaHamcrest/)
- [Tomakehurst (Great tool for mocking http services)](https://github.com/tomakehurst/wiremock)

### Testing
see `test/java/crawlerTest`

### Screenshots

Site Map Result
![](https://i.gyazo.com/f7453385fbbccc418b3fb7821e8ecdc4.gif)
![](https://i.gyazo.com/d6a11b822cfa35c37efcdfef644d5775.png)

Test Result
![Test results](https://i.gyazo.com/98f20ce74f04bb52bd8c2ff6eed3bae9.png)