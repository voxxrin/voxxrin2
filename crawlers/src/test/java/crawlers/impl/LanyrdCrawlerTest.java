package crawlers.impl;

import crawlers.configuration.CrawlingConfiguration;
import org.junit.Test;

import java.io.IOException;

public class LanyrdCrawlerTest {

    @Test
    public void should_properly_crawl_lanyrd_content() throws IOException {

        LeanKanbanFRCrawler leanKanbanFRCrawler = new LeanKanbanFRCrawler();

        leanKanbanFRCrawler.crawl(new CrawlingConfiguration().setExternalEventRef("2015/lkfr15"));
    }
}