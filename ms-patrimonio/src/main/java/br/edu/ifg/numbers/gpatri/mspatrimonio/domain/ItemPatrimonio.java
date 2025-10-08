package br.edu.ifg.numbers.gpatri.mspatrimonio.domain;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.CondicaoProduto;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.ItemEmUsoException;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.QuantidadeInvalidaException;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.QuantidadeItemIndisponivelException;
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

    @ManyToOne(targetEntity = Patrimonio.class, fetch = FetchType.LAZY)
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

    public void removeUnits(Integer units) {
        if (units == null) throw new QuantidadeInvalidaException("A quantidade solicitada não pode ser nula.");
        if (this.quantidade == 0) throw new ItemEmUsoException(String.format("O item '%s' ja se encontra em uso", this.id));
        if (this.quantidade < units)
            throw new QuantidadeItemIndisponivelException(
                    String.format("A quantidade solicitada do item '%s' é maior que a quantidade em estoque", this.id));
        this.quantidade -= units;
        this.updatedAt = Instant.now();
    }

    public void addUnits(Integer units) {
        if (units == null || units == 0) throw new QuantidadeInvalidaException(
                String.format("A quantidade solicitada não pode ser %s.", units == null ? "zero" : "nula")
        );
        this.quantidade += units;
        this.updatedAt = Instant.now();
    }

}
