package br.edu.ifg.numbers.gpatri.mspatrimonio.repository;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Condicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CondicaoRepository extends JpaRepository<Condicao, UUID> {
}
