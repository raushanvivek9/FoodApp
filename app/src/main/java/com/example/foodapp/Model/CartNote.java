package com.example.foodapp.Model;


public class CartNote {
    private String food_name;
    private Integer food_price;
    private Integer  quantity;
    private Integer total_price;
    private String hotelId;
    private String value;
    private String rest_name;

    public CartNote() {
    }

    public CartNote(String food_name, Integer food_price, Integer quantity, Integer total_price, String hotelId, String value, String rest_name) {
        this.food_name = food_name;
        this.food_price = food_price;
        this.quantity = quantity;
        this.total_price = total_price;
        this.hotelId = hotelId;
        this.value = value;
        this.rest_name = rest_name;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public Integer getFood_price() {
        return food_price;
    }

    public void setFood_price(Integer food_price) {
        this.food_price = food_price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getTotal_price() {
        return total_price;
    }

    public void setTotal_price(Integer total_price) {
        this.total_price = total_price;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getValue() {
        return value;
    }
    public String getRest_name() {
        return rest_name;
    }

    public void setRest_name(String rest_name) {
        this.rest_name = rest_name;
    }

    public void setValue(String value) {
        this.value = value;

    }


}
