package crawlers.configuration;

import org.jongo.marshall.jackson.oid.Id;

import java.util.ArrayList;
import java.util.List;

public class CrawlingConfiguration {

    /**
     * Internal event ref (ex = dvxfr15)
     */

    @Id
    private String eventId;

    /**
     * Event reference used in external systems (ex = DevoxxFR2015)
     */

    private String externalEventRef;

    /**
     * Crawler used to extract data from external systems
     */

    private String crawlerId;

    /**
     * Event related (extra) information
     */

    private String imageUrl;

    private String location;

    /**
     * Authorized tokens
     */

    private List<String> tokens = new ArrayList<>();

    public String getEventId() {
        return eventId;
    }

    public String getExternalEventRef() {
        return externalEventRef;
    }

    public String getCrawlerId() {
        return crawlerId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public CrawlingConfiguration setLocation(final String location) {
        this.location = location;
        return this;
    }

    public CrawlingConfiguration setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public CrawlingConfiguration setCrawlerId(final String crawlerId) {
        this.crawlerId = crawlerId;
        return this;
    }

    public CrawlingConfiguration setExternalEventRef(final String externalEventRef) {
        this.externalEventRef = externalEventRef;
        return this;
    }

    public CrawlingConfiguration setEventId(final String eventId) {
        this.eventId = eventId;
        return this;
    }

    public CrawlingConfiguration setTokens(final List<String> tokens) {
        this.tokens = tokens;
        return this;
    }
}
