package com.example.ccw.e_wasterecycling;

public class User {

    private String email;
    private String username;
    private String pass;
    private String phone;
    private String confirmpassword;
    private String address;

    public void setEmail(String email) {
        this.email = email;
    }

    private String imageUrl;


    public User(String email, String username, String pass, String phone, String confirmpassword, String address, String imageUrl) {
        this.email = email;
        this.username = username;
        this.pass = pass;
        this.phone = phone;
        this.confirmpassword = confirmpassword;
        this.address = address;
        this.imageUrl=imageUrl;


    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPass() {
        return pass;
    }

    public String getPhone() {
        return phone;
    }

    public String getConfirmpassword() {
        return confirmpassword;
    }

    public String getAddress() {
        return address;
    }

    public String getImageUrl() {
        return imageUrl;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setConfirmpassword(String confirmpassword) {
        this.confirmpassword = confirmpassword;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
