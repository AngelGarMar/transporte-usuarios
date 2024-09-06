package com.transporte.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
public class Permission {
    private String name;
    private List<Method> method;

    public Permission() {
        this.method = new ArrayList();
    }

    public void addPermission(Method method) {
        this.method.add(method);
    }
}
