package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class CategoriaUpdateDTO {

    private String nome;
    private UUID idCategoriaMae;

}
