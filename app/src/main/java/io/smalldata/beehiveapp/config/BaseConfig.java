package io.smalldata.beehiveapp.config;

import org.json.JSONArray;

/**
 * Parent config for all config classes to inherit from
 * Enforces constraints that all base classes should have
 * Created by fnokeke on 2/21/17.
 */

 abstract class BaseConfig {
    public abstract void saveSettings(JSONArray config);
}
