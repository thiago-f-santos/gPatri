package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class PatrimonioResponseDTO {

    private UUID id;

    private String nome;

    private String descricao;

    private Double precoEstimado;

    private UUID idCategoria;

    private String nomeCategoria;

}
