package com.autopublish.socials.entities;

import java.io.Serializable;
import java.util.Objects;

public class CustomerPageId implements Serializable {
    private String customer;
    private String page;

    public CustomerPageId() {}

    public CustomerPageId(String customer, String page) {
        this.customer = customer;
        this.page = page;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getPage() {
        return page;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerPageId that = (CustomerPageId) o;
        return Objects.equals(customer, that.customer) && Objects.equals(page, that.page);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customer, page);
    }

    public void setPage(String page) {
        this.page = page;
    }
}

