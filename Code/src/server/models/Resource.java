package server.models;

import java.io.Serializable;

public class Resource implements Serializable {
    // unique identifier for the resource
    private String resourceID;
    // title of the book or material
    private String title;
    // author of the resource
    private String author;
    // category or genre of the resource
    private String category;
    // availability status: true if available for borrowing
    private boolean isAvailable;

    // constructor initializes the resource and marks it as available
    public Resource(String resourceID, String title, String author, String category) {
        this.resourceID = resourceID;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isAvailable = true;
    }

    // attempts to borrow the resource; returns true if successful
    public boolean requestResource() {
        if (isAvailable) {
            isAvailable = false;
            return true;
        }
        return false;
    }

    // makes the resource available again
    public void returnResource() {
        isAvailable = true;
    }

    // getter methods for all fields
    public String getResourceID() { return resourceID; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public boolean isAvailable() { return isAvailable; }

    // setter methods for editable fields
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setCategory(String category) { this.category = category; }
}
