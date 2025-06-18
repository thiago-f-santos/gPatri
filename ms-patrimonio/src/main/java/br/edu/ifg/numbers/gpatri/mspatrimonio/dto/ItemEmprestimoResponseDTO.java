package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemEmprestimoResponseDTO {

    private ItemPatrimonioResponseDTO itemPatrimonio;

    private Integer quantidade;

}
