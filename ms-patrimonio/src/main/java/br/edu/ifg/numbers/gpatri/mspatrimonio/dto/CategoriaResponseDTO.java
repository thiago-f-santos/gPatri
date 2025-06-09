package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class CategoriaResponseDTO {

    private UUID id;

    private String nome;

    private UUID idCategoriaMae;

    private String categoriaMaeNome;

}
