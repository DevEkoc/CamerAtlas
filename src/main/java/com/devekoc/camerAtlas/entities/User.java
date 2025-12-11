package com.devekoc.camerAtlas.entities;

import com.devekoc.camerAtlas.enumerations.Role;
import com.devekoc.camerAtlas.validation.ValidCustomEmail;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Stream;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "utilisateur")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nom", unique = true)
    @NotNull(message = "Le nom ne doit pas être vide")
    private String name;

    @Column(name = "prenom")
    @NotNull(message = "Le prénom ne doit pas être vide")
    private String surname;

    @Column(name = "pseudo", unique = true)
    @NotNull(message = "Le pseudo ne doit pas être vide")
    private String pseudo;

    @Column(name = "motDePasse")
    @NotNull(message = "Le mot de passe ne doit pas être vide")
    private String password;

    @Column(name = "emailUtilisateur", unique = true)
    @NotNull(message = "L'adresse mail est obligatoire")
    @ValidCustomEmail
    private String email;

    @Column(name = "estActif")
    private boolean isActive = false;

    @Column(name = "estBloque")
    private boolean isLocked = false;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le rôle de l'utilisateur est obligatoire")
    private Role role;

    @Column(name = "dateCreation")
    private Instant createdAt;

    @Column(name = "derniereConnexion")
    private Instant lastConnection;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.concat(
                role.getPermissions().stream().map(p -> new SimpleGrantedAuthority(p.name())),
                Stream.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
        ).toList();

    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    } // Le compte n'expire jamais tant qu'il est actif

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    } // Retourne TRUE si le compte n'est pas vérrouillé

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    } // Les identifiants n'expirent pas tant qu'ils sont actifs

    @Override
    public boolean isEnabled() {
        return isActive && !isLocked;
    } // Le compte est utilisable si actif ET non verrouillé
}
