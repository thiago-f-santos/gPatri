package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Patrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.CondicaoProduto;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class ItemPatrimonioResponseDTO {

    private UUID id;

    private PatrimonioResponseDTO patrimonio;

    private CondicaoProduto condicaoProduto;

    private String condicaoDescricao;

    private Integer quantidade;

}
