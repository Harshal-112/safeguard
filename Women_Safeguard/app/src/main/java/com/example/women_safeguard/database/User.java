package com.example.women_safeguard.database;

import com.example.women_safeguard.watch.EmergencyContact;

import java.util.Map;

public class User {
    public String fullName, username, email, password, localAddress, contactNumber, emergency1, emergency2, emergency3;
    private Map<String, EmergencyContact> emergencyContacts;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String fullName, String username, String email, String password, String localAddress, String contactNumber, String emergency1, String emergency2, String emergency3) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.localAddress = localAddress;
        this.contactNumber = contactNumber;
        this.emergency1 = emergency1;
        this.emergency2 = emergency2;
        this.emergency3 = emergency3;
    }
    public String getEmergencyContacts() {
        return emergency1;
    }
    public User(String fullName, String username, String email, String localAddress, String contactNumber, String emergency1, String emergency2, String emergency3) {
    }
}