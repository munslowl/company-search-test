package net.munslow.companysearch.model;

import lombok.Data;

import java.util.List;

@Data
public class SearchResult {
    int totalResults;
    List<Company> items;
}
