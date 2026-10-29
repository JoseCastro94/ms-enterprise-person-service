package com.enterprise.app.context;

import static com.enterprise.app.constants.ConstantsApp.ID_TRANSACTION;

import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.jboss.logging.MDC;

@UtilityClass
public class TransactionLogContext {
    public static void setTransactionId(String transactionId) {
        MDC.put(ID_TRANSACTION, transactionId);
    }

    public static String getTransactionId() {
        Object transactionId = MDC.get(ID_TRANSACTION);
        return transactionId == null ? null : transactionId.toString();
    }

    public static String getOrCreateTransactionId() {
        String transactionId = getTransactionId();
        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString();
            setTransactionId(transactionId);
        }
        return transactionId;
    }

    public static void clear() {
        MDC.remove(ID_TRANSACTION);
    }
}
