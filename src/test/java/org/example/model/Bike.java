package org.example.model;

public class Bike extends Vehicle {
    private String id;
    private String nrOfWheels;

    public Bike(String id, String nrOfWheels) {
        this.id = id;
        this.nrOfWheels = nrOfWheels;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNrOfWheels() {
        return nrOfWheels;
    }

    public void setNrOfWheels(String nrOfWheels) {
        this.nrOfWheels = nrOfWheels;
    }
}
