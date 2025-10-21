package com.exalt_company.user_domain.shared;

import java.util.List;

/**
 * Represents a page of data in a paginated result set.
 * <p>
 * This record encapsulates the content of a single page along with
 * pagination metadata such as the page number and page size.
 * </p>
 *
 * @param <T> the type of elements contained in the page
 * @param content the list of elements in this page
 * @param page the current page number (zero-based or one-based depending on implementation)
 * @param size the maximum number of elements per page
 */
public record Page<T> (List<T> content, int page, int size) {}
