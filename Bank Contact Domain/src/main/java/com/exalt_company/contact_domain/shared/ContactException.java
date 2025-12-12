package com.exalt_company.contact_domain.shared;

import com.exalt_company.contact_domain.api.resource.OperationState;

public class ContactException extends Exception {
    private final OperationState operationState;

    public ContactException(String message, OperationState operationState) {
        super(message);
        this.operationState = operationState;
    }

    public OperationState getOperationState() {
        return operationState;
    }
}
