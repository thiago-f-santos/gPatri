package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class ItemPatrimonioResponseDTO {

    private UUID id;

    private PatrimonioResponseDTO patrimonio;

    private CondicaoResponseDTO condicao;

    private boolean emUso;

}
