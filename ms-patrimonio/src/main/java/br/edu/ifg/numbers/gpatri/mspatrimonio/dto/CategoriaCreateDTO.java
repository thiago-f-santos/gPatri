package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class CategoriaCreateDTO {

    @NotBlank(message = "O nome da categoria não pode estar vazio")
    private String nome;

    private UUID idCategoriaMae;

}