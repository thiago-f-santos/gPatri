package br.edu.ifg.numbers.gpatri.msusuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    String token;
    String username;
    List<String> permissoes;
}
