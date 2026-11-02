package com.enterprise.app.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "cache")
public interface CacheProperties {
    Ubigeo ubigeo();

    interface Ubigeo {
        @WithDefault("true")
        boolean enabled();

        @WithDefault("1800")
        long ttl();
    }
}
