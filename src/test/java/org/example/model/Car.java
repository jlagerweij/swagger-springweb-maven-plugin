package org.example.model;

import java.util.List;

public class Car extends Vehicle {
    private String id;
    private String name;
    private String color;
    private List<Wheel> wheels;

    public Car(String id, String name, String color, List<Wheel> wheels) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.wheels = wheels;
    }

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Wheel> getWheels() {
        return wheels;
    }

    public void setWheels(List<Wheel> wheels) {
        this.wheels = wheels;
    }
}
