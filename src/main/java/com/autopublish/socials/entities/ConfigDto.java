package com.autopublish.socials.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigDto {
    private List<CustomerFBPage> pages;

    public ConfigDto() {
        this.pages = new ArrayList<>();
    }

    public void addPage(CustomerFBPage page) {
        pages.add(page);
    }

    public void addPages(Set<CustomerFBPage> pages) {
        this.pages.addAll(pages);
    }

    public List<CustomerFBPage> getPages() {
        return pages;
    }

    public void setPages(List<CustomerFBPage> pages) {
        this.pages = pages;
    }
}
