package crawlers.impl;

import com.google.common.collect.ImmutableList;
import restx.factory.Component;

@Component
public class CodeursEnSeineCFPCrawler extends LanyrdCrawler {

    public CodeursEnSeineCFPCrawler() {
        super("ces", ImmutableList.of("ces-publisher"));
    }
}
