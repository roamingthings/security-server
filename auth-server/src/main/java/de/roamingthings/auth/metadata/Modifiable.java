package de.roamingthings.auth.metadata;

import java.util.Date;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/04/12
 */
public interface Modifiable {
    Date getModifiedAt();

    void setModifiedAt(Date modifiedAt);
}
