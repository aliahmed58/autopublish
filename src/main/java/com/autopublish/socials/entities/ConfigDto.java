package com.autopublish.socials.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigDto {
    private List<FBPage> pages;

    public ConfigDto() {
        this.pages = new ArrayList<>();
    }

    public void addPage(FBPage page) {
        pages.add(page);
    }

    public void addPages(Set<FBPage> pages) {
        this.pages.addAll(pages);
    }

    public List<FBPage> getPages() {
        return pages;
    }

    public void setPages(List<FBPage> pages) {
        this.pages = pages;
    }
}
