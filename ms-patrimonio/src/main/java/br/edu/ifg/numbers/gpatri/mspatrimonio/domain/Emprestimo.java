package br.edu.ifg.numbers.gpatri.mspatrimonio.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.*;

@Entity
@Data @NoArgsConstructor
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "id_usuario", nullable = false, updatable = false)
    private UUID idUsuario;

    @Column(name = "aprovado")
    private Boolean aprovado = false;

    @Column(name = "data_emprestimo", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataEmprestimo;

    @Column(name = "data_devolucao", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataDevolucao;

    @OneToMany(mappedBy = "emprestimo")
    private List<ItemEmprestimo> itensEmprestimo = new LinkedList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}
