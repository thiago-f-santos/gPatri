package br.edu.ifg.numbers.gpatri.mspatrimonio.domain;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.SituacaoEmprestimo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Data @NoArgsConstructor
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "id_usuario", nullable = false, updatable = false)
    private UUID idUsuario;

    @Column(name = "id_usuario_avaliador")
    private UUID idUsuarioAvaliador;

    @Column(name = "situacao", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private SituacaoEmprestimo situacao = SituacaoEmprestimo.EM_ESPERA;

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
