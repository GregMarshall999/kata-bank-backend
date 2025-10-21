package com.exalt_company.user_domain.api.resource;

/**
 * Represents an administrative response containing operation results.
 * <p>
 * This record encapsulates the result of administrative operations with a state,
 * optional content, and optional error message. It provides flexibility to represent
 * both successful and failed operations.
 * </p>
 *
 * @param <C> the type of content returned by the administrative operation
 * @param content the content of the response, may be null if the operation failed
 * @param state the state of the administrative response
 * @param errorMessage the error message if the operation failed, null otherwise
 */
public record AdminResponse<C>(C content, AdminResponseState state, String errorMessage) {
    /**
     * Creates a successful administrative response with content and state.
     *
     * @param content the content of the response
     * @param state the state of the administrative response
     */
    public AdminResponse(C content, AdminResponseState state) {
        this(content, state, null);
    }

    /**
     * Creates a failed administrative response with state and error message.
     *
     * @param state the state of the administrative response
     * @param errorMessage the error message describing the failure
     */
    public AdminResponse(AdminResponseState state, String errorMessage) {
        this(null, state, errorMessage);
    }
}
