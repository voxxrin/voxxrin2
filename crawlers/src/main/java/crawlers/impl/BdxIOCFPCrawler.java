package crawlers.impl;

import com.google.common.collect.ImmutableList;
import restx.factory.Component;

@Component
public class BdxIOCFPCrawler extends PartialDevoxxCFPCrawler {

    private static final String BASE_URL = "http://cfp.bdx.io/api/conferences/";

    public BdxIOCFPCrawler() {
        super("bdxio", ImmutableList.of("bdxio-publisher"), BASE_URL);
    }
}
