package com.barterAuctions.portal.models.user;

import javax.persistence.*;

@Entity
@Table(name = "ROLES")
public class Role {
    @Id
    private String role;

    public Role() {
    }

 /*   public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }*/

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
