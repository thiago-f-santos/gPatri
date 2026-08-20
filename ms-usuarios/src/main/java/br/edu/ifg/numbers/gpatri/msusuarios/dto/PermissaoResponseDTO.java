package br.edu.ifg.numbers.gpatri.msusuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissaoResponseDTO {
    private UUID id;
    private String nome;
    private String descricao;
    private String categoria;
}
