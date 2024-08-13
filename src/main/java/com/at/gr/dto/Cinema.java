package com.at.gr.dto;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by bogdan.kalika@gmail.com
 * Date: 8/12/2024
 */
public record Cinema(Long id,
                     String title,
                     Integer yearOfProduction,
                     Duration duration,
                     LocalDateTime screeningTime,
                     Integer hall) {
}
