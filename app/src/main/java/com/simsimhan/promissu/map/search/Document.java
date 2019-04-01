package com.simsimhan.promissu.map.search;

import com.simsimhan.promissu.model.LocationSearchItem;

import java.util.List;

public class Document {
    private List<LocationSearchItem> documents;

    public Document(List<LocationSearchItem> documents) {
        this.documents = documents;
    }

    public List<LocationSearchItem> getDocuments() {
        return documents;
    }

    public void setDocuments(List<LocationSearchItem> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "Document{" +
                "documents=" + documents +
                '}';
    }
}
