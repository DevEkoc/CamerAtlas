package com.devekoc.camerAtlas.entities;

import com.devekoc.camerAtlas.enumerations.SuggestionStatus;
import com.devekoc.camerAtlas.enumerations.SuggestionType;
import com.devekoc.camerAtlas.enumerations.TargetType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "suggestion")
@Getter @Setter
public class Suggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private SuggestionType type;

    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    private int targetId;     // id de l’entité concernée (null pour un CREATE)

    @Column(columnDefinition = "json")
    private String payload;    // Les nouvelles valeurs sous forme JSON

    @Enumerated(EnumType.STRING)
    private SuggestionStatus status;

    @ManyToOne
    @JoinColumn(name = "idUtilisateur")
    private User submittedBy;

    private Instant submittedAt;
}

