package crawlers.impl;

import java.io.IOException;

import crawlers.configuration.CrawlingConfiguration;

import org.junit.Test;


public class BreizhCampCrawlerTest {
    
    @Test
    public void should_crawl_breizhcamp_content() throws IOException {
        final BreizhCampCrawler breizhCampCrawler = new BreizhCampCrawler();
        breizhCampCrawler.crawl(new CrawlingConfiguration().setExternalEventRef("breizhcamp"));
    }
}
