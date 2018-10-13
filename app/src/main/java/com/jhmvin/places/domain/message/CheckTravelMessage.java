package com.jhmvin.places.domain.message;

public class CheckTravelMessage {

    private boolean travelling = false;

    public boolean isTravelling() {
        return travelling;
    }

    public void setTravelling(boolean travelling) {
        this.travelling = travelling;
    }
}
