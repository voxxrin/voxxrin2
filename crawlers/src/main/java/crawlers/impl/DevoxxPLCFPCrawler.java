package crawlers.impl;

import com.google.common.collect.ImmutableList;
import restx.factory.Component;

@Component
public class DevoxxPLCFPCrawler extends DevoxxCFPCrawler {

    private static final String BASE_URL = "http://cfp.devoxx.pl/api/conferences/";

    public DevoxxPLCFPCrawler() {
        super("devoxxpl", ImmutableList.of("devoxxpl-publisher"), BASE_URL);
    }
}
