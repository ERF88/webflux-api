package com.github.erf88.controller.exceptions;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Builder
@Data
public class StandardError implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private OffsetDateTime timestamp;
    private String path;
    private Integer status;
    private String error;
    private String message;

}
