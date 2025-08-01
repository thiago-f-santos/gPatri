package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.CondicaoProduto;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class ItemPatrimonioUpdateDTO {

    private UUID idPatrimonio;

    private CondicaoProduto condicaoProduto;

    private String condicaoDescricao;

    @Min(value = 0, message = "A quantidade não pode ser menor que 0")
    private Integer quantidade;

}
