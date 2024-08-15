package com.autopublish.socials.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class FBPage {

    @Id
    @JsonProperty("id")
    private String pageId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("category")
    private String category;

    @JsonIgnore
    @ManyToMany(mappedBy = "pages")
    private Set<Customer> customers = new HashSet<>();

    @JsonProperty("access_token")
    private String access_token;

    @Transient
    private List<String> tasks;

    @JsonIgnore
    private boolean publishEnabled;

    public FBPage(String pageId, String name, String category, Set<Customer> customers,
                  String access_token, List<String> tasks, boolean publishEnabled) {
        this.pageId = pageId;
        this.name = name;
        this.category = category;
        this.customers = customers;
        this.access_token = access_token;
        this.tasks = tasks;
        this.publishEnabled = publishEnabled;
    }

    public FBPage() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public Set<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }

    public boolean isPublishEnabled() {
        return publishEnabled;
    }

    public void setPublishEnabled(boolean publishEnabled) {
        this.publishEnabled = publishEnabled;
    }

    @Override
    public String toString() {
        return "FBPage{" +
                "pageId='" + pageId + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", customers=" + customers +
                ", access_token='" + access_token + '\'' +
                ", tasks=" + tasks +
                ", publishEnabled=" + publishEnabled +
                '}';
    }
}
