package crawlers.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import crawlers.AbstractHttpCrawler;
import crawlers.CrawlingResult;
import crawlers.utils.Functions;
import org.slf4j.Logger;
import restx.*;
import restx.factory.Component;
import restx.http.HttpStatus;
import restx.jackson.FrontObjectMapperFactory;
import restx.security.Permission;
import restx.security.RestxSecurityManager;

import javax.inject.Named;
import java.io.IOException;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;
import static restx.security.Permissions.*;

@Component
public class CrawlingRoute extends StdRoute {

    private static final Logger logger = getLogger(CrawlingRoute.class);

    private final ImmutableMap<String, AbstractHttpCrawler> crawlers;
    private final RestxSecurityManager securityManager;
    private final ObjectMapper objectMapper;

    public CrawlingRoute(Set<AbstractHttpCrawler> crawlers,
                         RestxSecurityManager securityManager,
                         @Named(FrontObjectMapperFactory.MAPPER_NAME) ObjectMapper objectMapper) {

        super("crawling", new StdRestxRequestMatcher("PUT", "/crawl"));
        this.securityManager = securityManager;
        this.objectMapper = objectMapper;
        this.crawlers = Maps.uniqueIndex(crawlers, Functions.CRAWLERS_MAP_INDEXER);
    }

    private void checkSecurity(RestxRequest req, AbstractHttpCrawler crawler) {
        Iterable<Permission> rolePermissions = Iterables.transform(crawler.getRoles(), Functions.ROLE_TO_PERMISSION);
        securityManager.check(req, allOf(isAuthenticated(), anyOf(Iterables.toArray(rolePermissions, Permission.class))));
    }

    @Override
    public void handle(RestxRequestMatch match, RestxRequest req, RestxResponse resp, RestxContext ctx) throws IOException {

        String crawlerId = req.getQueryParam("crawlerId").get();

        AbstractHttpCrawler crawler = crawlers.get(crawlerId);
        if (crawler == null) {
            throw new WebException(HttpStatus.NOT_FOUND);
        }

        checkSecurity(req, crawler);

        CrawlingResult result = null;
        try {
            result = crawler.crawl();
        } catch (IOException e) {
            logger.error("Error occured during crawling (crawlerId = " + crawlerId + ")", e);
        }

        resp.setStatus(HttpStatus.OK);
        resp.setContentType("application/json");

        objectMapper.writer().writeValue(resp.getOutputStream(), result);
    }
}
