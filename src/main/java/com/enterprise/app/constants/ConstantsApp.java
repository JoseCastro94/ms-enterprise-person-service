package com.enterprise.app.constants;

import java.time.ZoneId;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ConstantsApp {
    public static final String ID_TRANSACTION = "idTransaction";
    public static final String HEADER_TRANSACTION_ID = "X-Transaction-Id";
    public static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");
}
