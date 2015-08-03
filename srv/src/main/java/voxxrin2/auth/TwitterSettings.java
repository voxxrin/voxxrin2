package voxxrin2.auth;

import restx.config.Settings;
import restx.config.SettingsKey;

@Settings
public interface TwitterSettings {

    @SettingsKey(key = "oauth.twitter.apiKey")
    String oauthTwitterApiKey();

    @SettingsKey(key = "oauth.twitter.apiSecret")
    String oauthTwitterApiSecret();

    @SettingsKey(key = "oauth.twitter.accessToken")
    String oauthTwitterAccessToken();

    @SettingsKey(key = "oauth.twitter.tokenSecret")
    String oauthTwitterTokenSecret();
}
