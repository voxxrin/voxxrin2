package crawlers.impl;

import com.google.common.collect.ImmutableList;
import restx.factory.Component;

@Component
public class CodeursEnSeineCFPCrawler extends DevoxxCFPCrawler {

    private static final String BASE_URL = "http://cfp.codeursenseine.com/api/conferences/";

    public CodeursEnSeineCFPCrawler() {
        super("ces", ImmutableList.of("ces-publisher"), BASE_URL);
    }
}
