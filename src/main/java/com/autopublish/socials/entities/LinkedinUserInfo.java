package com.autopublish.socials.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkedinUserInfo {

    @JsonProperty(value = "given_name")
    private String name;

    @JsonProperty(value = "sub")
    private String sub;

    @JsonIgnore
    private String urn;

    private LinkedinUserInfo() {}

    public LinkedinUserInfo(String name, String sub) {
        this.name = name;
        this.sub = sub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public void setUrn() {
        urn = "urn:li:person:" + sub;
    }

    public String getUrn() {
        return urn;
    }


    @Override
    public String toString() {
        return "LinkedinUserInfo{" +
                "name='" + name + '\'' +
                ", sub='" + sub + '\'' +
                '}';
    }
}
