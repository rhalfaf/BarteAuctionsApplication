package com.barterAuctions.portal.models.user;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
public class Authorities implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne
    //@JoinColumn(name = "USER_NAME", referencedColumnName = "NAME")
    private User user;
    @OneToOne
    @JoinColumn(name = "AUTHORITY", referencedColumnName = "ROLE")
    private Role role;

    public Authorities() {
    }

    public Authorities(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role.getRole();
    }
}
