package voxxrin2.auth;

import restx.config.Settings;
import restx.config.SettingsKey;

@Settings
public interface OAuthSettings {

    @SettingsKey(key = "oauth.twitter.apiKey")
    String oauthTwitterApiKey();

    @SettingsKey(key = "oauth.twitter.apiSecret")
    String oauthTwitterApiSecret();

    @SettingsKey(key = "oauth.secrets.token")
    String oauthSecretsToken();
}
