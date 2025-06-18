package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.TipoControle;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class PatrimonioCreateDTO {

    @NotBlank(message = "O nome do patrimônio não pode estar vazio")
    private String nome;

    private String descricao;

    @DecimalMin(value = "0.0", message = "O preço não pode ser menor que 0.0")
    private Double precoEstimado;

    @NotNull(message = "O tipo de controle do patrimonio não pode ser nulo")
    private TipoControle tipoControle;

    @NotNull(message = "O id da categoria do patrimonio não pode ser nulo")
    private UUID idCategoria;

}
