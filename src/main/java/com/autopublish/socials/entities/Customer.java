package com.autopublish.socials.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class Customer {

    @Id
    private String username;

    private String password;

    // unique user is based on an fb user id, no custom auth
    private String fbUserId;

    private String name;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private Set<CustomerFBPage> customerPages = new HashSet<>();


    @Nullable
    private String fbUserAccessToken;

    public Customer() {

    }

    public Customer(String username, String password, String fbUserId, String name,
                    Set<CustomerFBPage> customerPages, @Nullable String fbUserAccessToken) {
        this.username = username;
        this.password = password;
        this.fbUserId = fbUserId;
        this.name = name;
        this.customerPages = customerPages;
        this.fbUserAccessToken = fbUserAccessToken;
    }

    public Customer(UserDetails userDetails) {
        this.username = userDetails.getUsername();
        this.password = userDetails.getPassword();
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFbUserId() {
        return fbUserId;
    }

    public void setFbUserId(String fbUserId) {
        this.fbUserId = fbUserId;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getFbUserAccessToken() {
        return fbUserAccessToken;
    }

    public void setFbUserAccessToken(@Nullable String fbUserAccessToken) {
        this.fbUserAccessToken = fbUserAccessToken;
    }

    public Set<CustomerFBPage> getCustomerPages() {
        return customerPages;
    }

    public void setCustomerPages(Set<CustomerFBPage> customerPages) {
        this.customerPages = customerPages;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", fbUserId=" + fbUserId +
                ", name='" + name + '\'' +
                ", custommerPages=" + customerPages +
                ", fbUserAccessToken='" + fbUserAccessToken + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(username, customer.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
