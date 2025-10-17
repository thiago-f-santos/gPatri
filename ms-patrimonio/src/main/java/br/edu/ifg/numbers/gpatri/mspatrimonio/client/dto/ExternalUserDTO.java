package br.edu.ifg.numbers.gpatri.mspatrimonio.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class ExternalUserDTO {

    private UUID id;
    private String nome;
    private String sobrenome;
    private String email;
    private String cargo;
    private UUID idCargo;

}
