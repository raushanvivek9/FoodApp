package com.example.foodapp.Model;

public class RestProfile {
    private String rest_name;
    private String city;
    private String Address;
    private  String documentId;
    private String image;

    public RestProfile() {
    }

    public RestProfile(String rest_name, String city, String address,String image) {
        this.rest_name = rest_name;
        this.city = city;
        Address = address;
        this.image=image;
    }
    public String getRest_name() {
        return rest_name;
    }

    public void setRest_name(String rest_name) {
        this.rest_name = rest_name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
