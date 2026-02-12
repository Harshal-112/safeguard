package com.example.women_safeguard.watch;

public class EmergencyContact {
    private String id;
    private String name;
    private String phoneNumber;
    private String relationship;

    // Required empty constructor for Firebase
    public EmergencyContact() {
    }

    public EmergencyContact(String id, String name, String phoneNumber, String relationship) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.relationship = relationship;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}