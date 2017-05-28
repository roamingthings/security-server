package de.roamingthings.auth.metadata;

import javax.persistence.PrePersist;
import java.util.Date;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/04/12
 */
public class CreatableEntityListener {
    @PrePersist
    public void updateCreatedInformation(Object entity) {
        if (entity instanceof Creatable) {
            Creatable creatable = (Creatable) entity;
            creatable.setCreatedAt(new Date());
        }
    }
}
