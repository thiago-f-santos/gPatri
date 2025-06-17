package br.edu.ifg.numbers.gpatri.msusuarios.domain;

import br.edu.ifg.numbers.gpatri.msusuarios.enums.PermissaoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "cargos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cargo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cargo_permissoes", joinColumns = @JoinColumn(name = "cargo_id"))
    @Column(name = "permissao")
    @Enumerated(EnumType.STRING)
    private Set<PermissaoEnum> permissoes = new HashSet<>();

    public Cargo(String nome) {
        this.nome = nome;
        this.permissoes = new HashSet<>();
    }

}
