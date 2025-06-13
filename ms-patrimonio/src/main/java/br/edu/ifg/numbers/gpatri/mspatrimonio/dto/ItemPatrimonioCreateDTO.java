package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.CondicaoProduto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class ItemPatrimonioCreateDTO {

    @NotNull(message = "O id de patrimônio não pode ser nulo")
    private UUID idPatrimonio;

    @NotBlank(message = "A condição do produto não pode ser nula ou vazia")
    private CondicaoProduto condicaoProduto;

    @NotBlank(message = "A descrição da condição não pode ser nula ou vazia")
    private String condicaoDescricao;

    @Min(value = 1, message = "A quantidade não pode ser menor que 1")
    private Integer quantidade;

}
