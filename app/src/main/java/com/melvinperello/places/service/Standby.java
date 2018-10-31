package com.melvinperello.places.service;

/**
 * Add status checking to a service,
 * to give signal if it should do some processes or not when the activity is not visible.
 */
public interface Standby {

    /**
     * Put the service to sleep mode. this does not necessarily mean that the entire
     * service will sleep, use the isStandby() as a flag for the process that should be
     * affected.
     */
    void setStanbyEnabled(boolean standbyMode);


    /**
     * Flag to check if the service is awake.
     *
     * @return boolean flag.
     */
    boolean isStandby();
}
