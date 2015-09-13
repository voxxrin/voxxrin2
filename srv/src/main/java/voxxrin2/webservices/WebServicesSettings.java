package voxxrin2.webservices;

import restx.config.Settings;
import restx.config.SettingsKey;

@Settings
public interface WebServicesSettings {

    @SettingsKey(key = "ws.ionic.app.id")
    String ionicAppId();

    @SettingsKey(key = "ws.ionic.app.privateKey")
    String ionicAppPrivateKey();
}

