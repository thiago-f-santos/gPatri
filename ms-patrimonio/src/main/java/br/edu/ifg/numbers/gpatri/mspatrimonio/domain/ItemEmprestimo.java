package br.edu.ifg.numbers.gpatri.mspatrimonio.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data @NoArgsConstructor
public class ItemEmprestimo {

    @EmbeddedId
    private ItemEmprestimoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idItemPatrimonio")
    @JoinColumn(name = "id_item_patrimonio")
    private ItemPatrimonio itemPatrimonio;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idEmprestimo")
    @JoinColumn(name = "id_emprestimo")
    private Emprestimo emprestimo;

    @Column(name = "quantidade",  nullable = false)
    private Integer quantidade;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}
