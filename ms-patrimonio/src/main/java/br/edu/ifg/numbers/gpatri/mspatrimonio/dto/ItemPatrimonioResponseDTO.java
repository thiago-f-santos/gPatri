package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.CondicaoProduto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class ItemPatrimonioResponseDTO {

    private UUID id;

    private UUID patrimonioId;

    private String nomePatrimonio;

    private CondicaoProduto condicaoProduto;

    private String condicaoDescricao;

    private Integer quantidade;

}
