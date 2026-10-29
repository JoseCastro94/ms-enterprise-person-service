package com.enterprise.app.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "personas")
public interface ApplicationProperties {
    Batch batch();

    Archivo archivo();

    interface Batch {
        @WithDefault("100")
        int tamanoLote();
    }

    interface Archivo {
        @WithDefault("20971520")
        long maximoBytes();
    }
}
