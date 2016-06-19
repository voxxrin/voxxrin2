package crawlers.impl;

import com.google.common.collect.ImmutableList;
import restx.factory.Component;

@Component
public class DevoxxFRCFPCrawler extends PartialDevoxxCFPCrawler {

    private static final String BASE_URL = "http://cfp.devoxx.fr/api/conferences/";

    public DevoxxFRCFPCrawler() {
        super("devoxxfr", ImmutableList.of("devoxxfr-publisher"), BASE_URL);
    }
}
