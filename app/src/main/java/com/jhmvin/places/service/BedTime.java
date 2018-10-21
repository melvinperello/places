package com.jhmvin.places.service;

/**
 * Add status checking to a service,
 * to give signal if it should do some processes or not when the activity is not visible.
 */
public interface BedTime {

    /**
     * Put the service to sleep mode. this does not necessarily mean that the entire
     * service will sleep, use the isAwake() as a flag for the process that should be
     * affected.
     */
    void sleepMode();

    /**
     * Wake the service up and continue the affected process with the isAwake() flag.
     */
    void wakeMode();

    /**
     * Flag to check if the service is awake.
     *
     * @return boolean flag.
     */
    boolean isAwake();
}
