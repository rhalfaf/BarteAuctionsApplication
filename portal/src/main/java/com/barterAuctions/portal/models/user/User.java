package com.barterAuctions.portal.models.user;

import com.barterAuctions.portal.models.auction.Auction;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @NotNull
    @NotBlank(message = "Nazwa użytkownika jest wymagana.")
    @Size(min = 2, max = 32, message = "Nazwa użytkownika musi posiadać od 2 do 32 znaków")
    private String name;
    @NotNull
    @NotBlank(message = "Hasło jest wymagane.")
    private String password;
    @NotNull
    private boolean enabled;
    @NotNull
    @NotBlank(message = "Proszę podać email.")
    @Email(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",
    message = "Nie poprawny format adresu email")
    private String email;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Auction> auctions;
    @OneToOne(cascade = CascadeType.ALL)
    private Authorities authorities;
    @OneToMany(cascade = CascadeType.MERGE)
    private List<Auction> observedAuctions;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Auction> getAuctions() {
        return auctions;
    }

    public void setAuctions(List<Auction> auctions) {
        this.auctions = auctions;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Authorities getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Authorities authorities) {
        this.authorities = authorities;
    }

    public List<Auction> getObservedAuctions() {
        return observedAuctions;
    }

    public void setObservedAuctions(List<Auction> observedAuctions) {
        this.observedAuctions = observedAuctions;
    }
}
