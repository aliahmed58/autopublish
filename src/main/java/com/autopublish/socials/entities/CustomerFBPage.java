package com.autopublish.socials.entities;

import jakarta.persistence.*;

@Entity
@IdClass(CustomerPageId.class)
public class CustomerFBPage {

    @Id
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "page_id")
    private FBPage page;

    private boolean publishEnabled;

    private String accessToken;

    public CustomerFBPage() {

    }


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public FBPage getPage() {
        return page;
    }

    public void setPage(FBPage page) {
        this.page = page;
    }

    public boolean isPublishEnabled() {
        return publishEnabled;
    }

    public void setPublishEnabled(boolean publishEnabled) {
        this.publishEnabled = publishEnabled;
    }


}
