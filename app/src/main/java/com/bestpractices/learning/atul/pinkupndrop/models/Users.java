package com.bestpractices.learning.atul.pinkupndrop.models;

public class Users {
    private boolean serviceable;
    public Users(boolean serviceability){
        this.serviceable = serviceability;
    }

    public boolean isServiceability() {
        return serviceable;
    }

    public void setServiceability(boolean serviceability) {
        this.serviceable = serviceability;
    }
}
