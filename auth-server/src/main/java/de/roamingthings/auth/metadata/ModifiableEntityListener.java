package de.roamingthings.auth.metadata;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/04/12
 */
public class ModifiableEntityListener {
    @PrePersist
    @PreUpdate
    public void updateModifiedInformation(Object entity) {
        if (entity instanceof Modifiable) {
            Modifiable modifiable = (Modifiable) entity;
            modifiable.setModifiedAt(new Date());
        }
    }
}
