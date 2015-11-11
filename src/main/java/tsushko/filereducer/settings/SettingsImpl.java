package tsushko.filereducer.settings;

import akka.actor.Extension;
import com.typesafe.config.Config;

/**
 * Represents application-specific settings from configuration
 *
 * @author Artem Tsushko
 * @version 1.0
 */
public class SettingsImpl implements Extension {

    public final int HASH_MAP_CAPACITY;
    public final float HASH_MAP_LOAD_FACTOR;

    public SettingsImpl(Config config) {
        HASH_MAP_CAPACITY = config
                .getInt("file-reducer.hash-map.capacity");
        HASH_MAP_LOAD_FACTOR = (float) config
                .getDouble("file-reducer.hash-map.load-factor");

    }

}