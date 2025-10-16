package com.exalt_company.user_domain.domain.message;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record Message<F> (
        UUID id,
        LocalDate date,
        String content,
        String author,
        String recipient,
        List<String> copies,
        List<String> hiddenCopies,
        List<F> attachedFiles
){}
