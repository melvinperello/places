package com.jhmvin.places.domain.message;

public class CheckItineraryMessage {

    private String origin;
    private String destination;
    private long started;

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public long getStarted() {
        return started;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setStarted(long started) {
        this.started = started;
    }
}
