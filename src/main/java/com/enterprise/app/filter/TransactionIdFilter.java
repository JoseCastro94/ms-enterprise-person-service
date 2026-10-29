package com.enterprise.app.filter;

import static com.enterprise.app.constants.ConstantsApp.HEADER_TRANSACTION_ID;
import static com.enterprise.app.constants.ConstantsApp.ID_TRANSACTION;

import com.enterprise.app.context.TransactionLogContext;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.UUID;

@Provider
public class TransactionIdFilter implements ContainerRequestFilter, ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String transactionId = requestContext.getHeaderString(HEADER_TRANSACTION_ID);
        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
        }
        requestContext.setProperty(ID_TRANSACTION, transactionId);
        TransactionLogContext.setTransactionId(transactionId);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        Object transactionId = requestContext.getProperty(ID_TRANSACTION);
        if (transactionId == null) {
            transactionId = TransactionLogContext.getTransactionId();
        }
        if (transactionId != null) {
            responseContext.getHeaders().putSingle(HEADER_TRANSACTION_ID, transactionId.toString());
        }
        TransactionLogContext.clear();
    }
}
