package com.exalt_company.user_domain.domain.shared;

import java.util.List;

public record Page<T> (List<T> content, int page, int size) {}
