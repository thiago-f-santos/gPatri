package br.edu.ifg.numbers.gpatri.mspatrimonio.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data @NoArgsConstructor
@EqualsAndHashCode(exclude = {"categoriaMae", "subcategorias"})
@ToString(exclude = {"categoriaMae", "subcategorias"})
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid", unique = true, nullable = false)
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_mae")
    private Categoria categoriaMae;

    @OneToMany(mappedBy = "categoriaMae", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Categoria> subcategorias = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}