package br.edu.ifg.numbers.msusuarios.dto;

import br.edu.ifg.numbers.msusuarios.domain.Cargo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private UUID id;
    private String nome;
    private String sobrenome;
    private String email;
    private String cargo;
}
