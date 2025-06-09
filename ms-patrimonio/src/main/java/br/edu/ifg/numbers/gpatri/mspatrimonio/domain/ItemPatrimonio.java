package br.edu.ifg.numbers.gpatri.mspatrimonio.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data @NoArgsConstructor
public class ItemPatrimonio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid", unique = true, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_patrimonio")
    private Patrimonio patrimonio;

    @ManyToOne
    @JoinColumn(name = "id_condicao")
    private Condicao condicao;

    @Column(name = "em_uso", columnDefinition = "boolean")
    private boolean emUso;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}
