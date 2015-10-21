package crawlers.web;

import restx.config.Settings;
import restx.config.SettingsKey;

@Settings
public interface CrawlingSettings {

    @SettingsKey(key = "voxxrin.api.url")
    public String voxxrinBackendUrl();
}
