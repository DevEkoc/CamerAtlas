package com.devekoc.camerAtlas.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    @Column(unique = true, nullable = false)
    private String token;

    @Column
    private Instant deliveryDate;

    @Column(nullable = false)
    private Instant expiryDate;

    private boolean revoked = false;

    @ManyToOne
    @JoinColumn(name = "idUtilisateur")
    private User user;
}
