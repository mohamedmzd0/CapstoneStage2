package com.example.mohamedabdelaziz.marketstore;

/**
 * Created by Mohamed Abd Elaziz on 8/7/2017.
 */

public class UserDataTypes {
    public String name, email, phone, work, address, ProfileImage;

    public UserDataTypes() {
    }

    public UserDataTypes(String name, String email, String phone, String work, String address, String ProfileImage) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.work = work;
        this.address = address;
        this.ProfileImage = ProfileImage;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
