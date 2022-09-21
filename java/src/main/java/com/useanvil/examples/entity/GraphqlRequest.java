package com.useanvil.examples.entity;

public class GraphqlRequest {
    String query;
    String variables;

    public GraphqlRequest(String query, String variables) {
        this.query = query;
        this.variables = variables;
    }
}
