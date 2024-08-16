package com.autopublish.socials.entities;

import jakarta.persistence.*;

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
    private Customer customer;

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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Entry(Long entryId, Customer customer, int rating, String description) {
        this.entryId = entryId;
        this.customer = customer;
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
