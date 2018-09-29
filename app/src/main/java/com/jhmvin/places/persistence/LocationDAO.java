package com.jhmvin.places.persistence;

import java.util.List;

public interface LocationDAO {
    List<LocationPOJO> getLocations();

    boolean insert(LocationPOJO location);

    boolean delete(LocationPOJO location);

}
