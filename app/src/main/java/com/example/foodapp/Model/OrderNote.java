package com.example.foodapp.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;


public class OrderNote {
    private String order;
    private Integer total_price;
    private String hotelId;
    private String cust_name;
    private String pnr_no;
    private String phone_no;
    private String train_no;
    private String berth;
    private String rest_name;
    private String order_id;
    private String cust_id;
    private String pay_method;
    private String status;
    private @ServerTimestamp Date timestamp;
    public OrderNote() {
    }

    public OrderNote(String order, Integer total_price, String hotelId, String cust_name, String pnr_no, String phone_no, String train_no, String berth, String rest_name, String order_id, String cust_id, String pay_method, String status) {
        this.order = order;
        this.total_price = total_price;
        this.hotelId = hotelId;
        this.cust_name = cust_name;
        this.pnr_no = pnr_no;
        this.phone_no = phone_no;
        this.train_no = train_no;
        this.berth = berth;
        this.rest_name = rest_name;
        this.order_id = order_id;
        this.cust_id = cust_id;
        this.pay_method = pay_method;
        this.status = status;
    }

    public OrderNote(String order, Integer total_price, String hotelId, String cust_name, String pnr_no, String phone_no, String train_no, String berth, String rest_name, String order_id, String cust_id, String pay_method, String status, Date timestamp) {
        this.order = order;
        this.total_price = total_price;
        this.hotelId = hotelId;
        this.cust_name = cust_name;
        this.pnr_no = pnr_no;
        this.phone_no = phone_no;
        this.train_no = train_no;
        this.berth = berth;
        this.rest_name = rest_name;
        this.order_id = order_id;
        this.cust_id = cust_id;
        this.pay_method = pay_method;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
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

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getPnr_no() {
        return pnr_no;
    }

    public void setPnr_no(String pnr_no) {
        this.pnr_no = pnr_no;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getTrain_no() {
        return train_no;
    }

    public void setTrain_no(String train_no) {
        this.train_no = train_no;
    }

    public String getBerth() {
        return berth;
    }

    public void setBerth(String berth) {
        this.berth = berth;
    }

    public String getRest_name() {
        return rest_name;
    }

    public void setRest_name(String rest_name) {
        this.rest_name = rest_name;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getCust_id() {
        return cust_id;
    }

    public void setCust_id(String cust_id) {
        this.cust_id = cust_id;
    }

    public String getPay_method() {
        return pay_method;
    }

    public void setPay_method(String pay_method) {
        this.pay_method = pay_method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
