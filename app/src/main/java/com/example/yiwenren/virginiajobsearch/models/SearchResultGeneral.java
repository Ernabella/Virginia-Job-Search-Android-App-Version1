package com.example.yiwenren.virginiajobsearch.models;

/**
 * Created by yiwenren on 12/6/16.
 */

public class SearchResultGeneral {
    private int totalDocs;

    private SearchResult[] docs;

    public SearchResultGeneral(int totalDocs, SearchResult[] docs) {
        this.totalDocs = totalDocs;
        this.docs = docs;
    }

    public SearchResult[] getDocs() {
        return docs;
    }

    public void setDocs(SearchResult[] docs) {
        this.docs = docs;
    }

    public int getTotalDocs() {
        return totalDocs;
    }

    public void setTotalDocs(int totalDocs) {
        this.totalDocs = totalDocs;
    }

}
