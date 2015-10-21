package crawlers.utils;

import com.google.common.base.Function;
import crawlers.AbstractHttpCrawler;
import restx.security.Permission;

import static restx.security.Permissions.hasRole;

public class Functions {

    public static final Function<String, Permission> ROLE_TO_PERMISSION = new Function<String, Permission>() {
        @Override
        public Permission apply(String input) {
            return hasRole(input);
        }
    };
    public static Function<AbstractHttpCrawler, String> CRAWLERS_MAP_INDEXER = new Function<AbstractHttpCrawler, String>() {
        @Override
        public String apply(AbstractHttpCrawler input) {
            return input.getId();
        }
    };
}
