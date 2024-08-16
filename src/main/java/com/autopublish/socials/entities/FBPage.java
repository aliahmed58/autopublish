package com.autopublish.socials.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
    @OneToMany(mappedBy = "page")
    private Set<CustomerFBPage> customerPages = new HashSet<>();

    @Transient
    private List<String> tasks;

    public FBPage(String pageId, String name, String category, Set<CustomerFBPage> customerPages,
                  List<String> tasks) {
        this.pageId = pageId;
        this.name = name;
        this.category = category;
        this.customerPages = customerPages;
        this.tasks = tasks;
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

    public Set<CustomerFBPage> getCustomerPages() {
        return customerPages;
    }

    public void setCustomerPages(Set<CustomerFBPage> customerPages) {
        this.customerPages = customerPages;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "FBPage{" +
                "pageId='" + pageId + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", customers=" + customerPages +
                ", tasks=" + tasks +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FBPage fbPage)) return false;
        return Objects.equals(pageId, fbPage.pageId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pageId);
    }
}
