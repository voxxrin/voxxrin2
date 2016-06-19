package crawlers.impl;

import com.google.common.collect.ImmutableList;
import restx.factory.Component;

@Component
public class LeanKanbanFRCrawler extends LanyrdCrawler {

    public LeanKanbanFRCrawler() {
        super("lkfr", ImmutableList.of("lkfr-publisher"));
    }
}
