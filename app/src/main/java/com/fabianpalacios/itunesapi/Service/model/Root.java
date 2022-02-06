package com.fabianpalacios.itunesapi.Service.model;

import java.util.List;

public class Root {
    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}

