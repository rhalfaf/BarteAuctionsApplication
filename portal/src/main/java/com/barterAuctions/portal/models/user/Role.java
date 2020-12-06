package com.barterAuctions.portal.models.user;

import javax.persistence.*;

@Entity
@Table(name = "ROLES")
public class Role {
    @Id
    private String role;

    public Role() {
    }

    public Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
