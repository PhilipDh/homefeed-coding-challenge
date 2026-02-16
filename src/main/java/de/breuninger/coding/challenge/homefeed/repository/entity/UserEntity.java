package de.breuninger.coding.challenge.homefeed.repository.entity;

import java.util.Set;

public class UserEntity {
    private Long id;
    private String userId;
    private String firstname;
    private String surname;

    private Set<String> preferredCategories;  // e.g., ["FASHION", "ELECTRONICS"] TODO if I have time later I can make this a table
    private String userSegment;  // e.g., "NEW", "RETURNING", "VIP" TODO same here

    private Integer totalOrders;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Set<String> getPreferredCategories() {
        return preferredCategories;
    }

    public void setPreferredCategories(Set<String> preferredCategories) {
        this.preferredCategories = preferredCategories;
    }

    public String getUserSegment() {
        return userSegment;
    }

    public void setUserSegment(String userSegment) {
        this.userSegment = userSegment;
    }


    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }
}
