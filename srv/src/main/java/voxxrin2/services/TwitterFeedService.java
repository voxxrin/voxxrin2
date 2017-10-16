package voxxrin2.services;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import restx.factory.Component;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import voxxrin2.auth.OAuthSettings;
import voxxrin2.domain.Event;
import voxxrin2.domain.Tweet;

import java.util.List;

import static twitter4j.Logger.getLogger;

@Component
public class TwitterFeedService {

    private static final Logger logger = getLogger(TwitterFeedService.class);

    private final Twitter twitter;

    public TwitterFeedService(OAuthSettings oAuthSettings) {
        this.twitter = this.buildTwitterApiConsumer(oAuthSettings);
    }

    private Twitter buildTwitterApiConsumer(OAuthSettings oAuthSettings) {
        ConfigurationBuilder cb = new ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthConsumerKey(oAuthSettings.oauthTwitterApiKey())
                .setOAuthConsumerSecret(oAuthSettings.oauthTwitterApiSecret())
                .setOAuthAccessToken(oAuthSettings.oauthTwitterAccessToken())
                .setOAuthAccessTokenSecret(oAuthSettings.oauthTwitterAccessTokenSecret());
        TwitterFactory twitter = new TwitterFactory(cb.build());
        return twitter.getInstance();
    }

    public Function<Event, List<Tweet>> toTweetsFeed() {
        return new Function<Event, List<Tweet>>() {
            @Override
            public List<Tweet> apply(Event event) {
                Query query = new Query(event.getHashTag());
                query.setCount(50);
                try {
                    QueryResult result = twitter.search(query);
                    return FluentIterable.from(result.getTweets()).filter(filterTweet()).transform(toTweet()).toList();
                } catch (TwitterException e) {
                    logger.error("unable to fetch twitter feed", e);
                }
                return Lists.newArrayList();
            }
        };
    }

    private Predicate<Status> filterTweet() {
        return new Predicate<Status>() {
            @Override
            public boolean apply(Status input) {
                return !input.isRetweet();
            }
        };
    }

    private Function<Status, Tweet> toTweet() {
        return new Function<Status, Tweet>() {
            @Override
            public Tweet apply(Status input) {
                return new Tweet()
                        .setText(input.getText())
                        .setCreatedAt(input.getCreatedAt())
                        .setAuthorName(input.getUser().getScreenName())
                        .setAuthorImage(input.getUser().getBiggerProfileImageURL());
            }
        };
    }
}
