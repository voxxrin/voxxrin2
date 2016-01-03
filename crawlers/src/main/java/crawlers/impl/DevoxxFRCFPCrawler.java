package crawlers.impl;

import com.google.common.collect.ImmutableList;
import restx.factory.Component;

@Component
public class DevoxxFRCFPCrawler extends DevoxxCFPCrawler {

    private static final String DEVOXXFR_CFP_BASE_URL = "http://cfp.devoxx.fr/api/conferences/";

    public DevoxxFRCFPCrawler() {
        super("devoxxfr", ImmutableList.of("devoxxfr-publisher"), DEVOXXFR_CFP_BASE_URL);
    }
}
