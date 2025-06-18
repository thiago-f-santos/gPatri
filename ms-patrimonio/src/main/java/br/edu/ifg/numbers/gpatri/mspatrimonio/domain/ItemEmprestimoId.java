package br.edu.ifg.numbers.gpatri.mspatrimonio.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data @NoArgsConstructor
@EqualsAndHashCode
public class ItemEmprestimoId implements Serializable {

    private UUID idItemPatrimonio;

    private UUID idEmprestimo;

}
