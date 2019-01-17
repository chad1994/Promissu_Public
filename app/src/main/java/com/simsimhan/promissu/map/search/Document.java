package com.simsimhan.promissu.map.search;

import java.util.List;

public class Document {
    private List<Item> documents;

    public Document(List<Item> documents) {
        this.documents = documents;
    }

    public List<Item> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Item> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "Document{" +
                "documents=" + documents +
                '}';
    }
}
