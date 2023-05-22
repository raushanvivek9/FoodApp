package com.example.foodapp.Model;

import com.google.firebase.firestore.Exclude;

import java.util.List;

public class HomeNote {
    private List<String> station_name;
    private Integer train_no;
    private  String documentId;

    public HomeNote(List<String> station_name, Integer train_no) {
        this.station_name = station_name;
        this.train_no = train_no;
    }

    public HomeNote() {
    }
    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public List<String> getStation_name() {
        return station_name;
    }

    public void setStation_name(List<String> station_name) {
        this.station_name = station_name;
    }

    public Integer getTrain_no() {
        return train_no;
    }

    public void setTrain_no(Integer train_no) {
        this.train_no = train_no;
    }

}