package crawlers;

import restx.config.Settings;
import restx.config.SettingsKey;

@Settings
public interface CrawlingSettings {

    @SettingsKey(key = "voxxrin.api.url")
    String voxxrinBackendUrl();

    @SettingsKey(key = "voxxrin.http.basic.pwd")
    String voxxrinAdminHttpBasic();
}
