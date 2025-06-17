package br.edu.ifg.numbers.gpatri.mspatrimonio.domain;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.CondicaoProduto;
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

    @Column(name = "condicao_produto")
    @Enumerated(EnumType.STRING)
    private CondicaoProduto condicaoProduto;

    @Column(name = "condicao_descricao")
    private String condicaoDescricao;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}
