package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.TipoControle;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class PatrimonioUpdateDTO {

    private String nome;

    private String descricao;

    @DecimalMin(value = "0.0", message = "O preço não pode ser menor que 0.0")
    private Double precoEstimado;

    private UUID idCategoria;

    private TipoControle tipoControle;

}
