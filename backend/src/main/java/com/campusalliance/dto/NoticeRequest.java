package com.campusalliance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class NoticeRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    // client sends this back on updates so JPA can detect conflicts
    // null on create (new notice has no version yet)
    private Integer version;
}
