package com.avatlantik.cooperative.model;

public class LandingMember {

    private int id;
    private String name;
    private Double litres;
    private Double fat;

    public LandingMember(int id, String name, Double fat, Double litres) {
        this.id = id;
        this.name = name;
        this.litres = litres;
        this.fat = fat;
    }

    public LandingMember(int id, String name) {
        this.id = id;
        this.name = name;
        this.litres = null;
        this.fat = null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getLitres() {
        return litres;
    }

    public Double getFat() {
        return fat;
    }
}
