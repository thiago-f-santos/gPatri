package br.edu.ifg.numbers.gpatri.msusuarios.repository;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissaoRepository extends JpaRepository<Permissao, UUID> {
    Optional<Permissao> findByNome(String nome);
    List<Permissao> findAllByOrderByCategoriaAscNomeAsc();
    List<Permissao> findByCategoriaOrderByNomeAsc(String categoria);
}
