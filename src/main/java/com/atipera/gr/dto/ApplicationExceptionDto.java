package com.atipera.gr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by bogdan.kalika@gmail.com
 * Date: 7/28/2024
 */
@Data
@AllArgsConstructor
public class ApplicationExceptionDto {

    private int status;

    private String message;
}
