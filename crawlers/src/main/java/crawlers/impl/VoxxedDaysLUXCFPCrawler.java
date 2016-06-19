package crawlers.impl;

import com.google.common.collect.ImmutableList;
import restx.factory.Component;

@Component
public class VoxxedDaysLUXCFPCrawler extends PartialDevoxxCFPCrawler {

    private static final String BASE_URL = "http://cfp-voxxed-lux.yajug.org/api/conferences/";

    public VoxxedDaysLUXCFPCrawler() {
        super("vxdlux", ImmutableList.of("vxdlux-publisher"), BASE_URL);
    }
}
