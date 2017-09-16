package voxxrin2.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import restx.factory.Component;
import restx.jackson.FrontObjectMapperFactory;
import voxxrin2.domain.Event;
import voxxrin2.domain.Tweet;

import javax.inject.Named;
import java.io.IOException;
import java.util.List;

@Component
public class TwitterFeedService {

    private final ObjectMapper mapper;
    private final String header;

    public TwitterFeedService(@Named(FrontObjectMapperFactory.MAPPER_NAME) ObjectMapper mapper,
                              @Named("oauth.twitter.header") String header) {
        this.mapper = mapper;
        this.header = header;
    }

    public Function<Event, List<Tweet>> toTweetsFeed() {
        return new Function<Event, List<Tweet>>() {
            @Override
            public List<Tweet> apply(Event event) {
                try {
                    ArrayNode nodes = (ArrayNode) mapper.readTree(buildHttpRequest(event).body()).get("statuses");
                    return FluentIterable.from(nodes).transform(new Function<JsonNode, Tweet>() {
                        @Override
                        public Tweet apply(JsonNode input) {
                            return new Tweet()
                                    .setAuthor(input.at("/user/name").textValue())
                                    .setText(input.at("/text").textValue())
                                    .setCreatedAt(input.at("/created_at").textValue());
                        }
                    }).toList();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private HttpRequest buildHttpRequest(Event event) {
        String url = "https://api.twitter.com/1.1/search/tweets.json";
        String params = "q=%23" + event.getHashTag();
        String oauthHeader = "OAuth " + header;
        return HttpRequest.get(url + "?" + params).header("Authorization", oauthHeader);
    }
}
