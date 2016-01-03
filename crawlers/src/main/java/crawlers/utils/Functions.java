package crawlers.utils;

import com.google.common.base.Function;
import crawlers.AbstractHttpCrawler;

public class Functions {

    public static Function<AbstractHttpCrawler, String> CRAWLERS_MAP_INDEXER = new Function<AbstractHttpCrawler, String>() {
        @Override
        public String apply(AbstractHttpCrawler input) {
            return input.getId();
        }
    };
}
