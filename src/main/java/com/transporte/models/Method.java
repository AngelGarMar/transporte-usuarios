package com.transporte.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Method {
    private String name;
    private String path;
    private String httpMethod;
    private boolean permitAll;
    private Base base;
}
