package br.edu.ifg.numbers.gpatri.mspatrimonio.domain;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.TipoControle;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Data @NoArgsConstructor
public class Patrimonio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid", unique = true, nullable = false)
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "preco_estimado", nullable = false)
    private Double precoEstimado;

    @Column(name = "tipo_controle")
    @Enumerated(EnumType.STRING)
    private TipoControle tipoControle;

    @OneToMany(mappedBy = "patrimonio", targetEntity = ItemPatrimonio.class, fetch = FetchType.EAGER)
    private List<ItemPatrimonio> itensPatrimonio = new LinkedList<>();

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}
