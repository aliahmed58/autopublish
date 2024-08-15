package com.autopublish.socials.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;

/*
*  Using minimal fields
* */
@Entity
public class Entry {
    @Id
    @GeneratedValue
    private Long entryId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customerId", referencedColumnName = "username")
    private Customer customerId;

    private int rating;

    @Column(length = 3000, columnDefinition = "Text")
    private String description;

    public Entry() {
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public Customer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Customer customerId) {
        this.customerId = customerId;
    }

    public Entry(Long entryId, Customer customerId, int rating, String description) {
        this.entryId = entryId;
        this.customerId = customerId;
        this.rating = rating;
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
