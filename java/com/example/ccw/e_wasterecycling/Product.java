package com.example.ccw.e_wasterecycling;

public class Product {


    public String email, uid, address, condition, imageUrl, from_available_date,
            to_available_date, from_available_time, to_available_time, category, status;

    public Product() {

    }

    public Product(String email, String uid, String address, String condition,
                   String imageUrl, String from_available_date, String to_available_date,
                   String from_available_time, String to_available_time, String category, String status) {
        this.email = email;
        this.uid = uid;
        this.address = address;
        this.condition = condition;
        this.imageUrl = imageUrl;
        this.from_available_date = from_available_date;
        this.to_available_date = to_available_date;
        this.from_available_time = from_available_time;
        this.to_available_time = to_available_time;
        this.category = category;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getAddress() {
        return address;
    }

    public String getCondition() {
        return condition;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getFrom_available_date() {
        return from_available_date;
    }

    public String getTo_available_date() {
        return to_available_date;
    }

    public String getFrom_available_time() {
        return from_available_time;
    }

    public String getTo_available_time() {
        return to_available_time;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }
}
