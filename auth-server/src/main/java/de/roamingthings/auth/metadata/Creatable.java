package de.roamingthings.auth.metadata;

import java.util.Date;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/04/12
 */
public interface Creatable {
    Date getCreatedAt();

    void setCreatedAt(Date date);
}
