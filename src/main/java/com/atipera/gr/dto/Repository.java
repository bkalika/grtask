package com.atipera.gr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by bogdan.kalika@gmail.com
 * Date: 7/26/2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository {

    private String name;

    private Owner owner;

    private boolean fork;

    private String branchesUrl;
}
