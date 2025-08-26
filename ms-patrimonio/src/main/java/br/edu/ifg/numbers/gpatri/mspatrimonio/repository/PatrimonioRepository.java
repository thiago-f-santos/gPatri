package br.edu.ifg.numbers.gpatri.mspatrimonio.repository;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Patrimonio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PatrimonioRepository extends JpaRepository<Patrimonio, UUID> {

    @Query(value = "SELECT DISTINCT p FROM Patrimonio p JOIN FETCH p.itensPatrimonio ip WHERE ip.quantidade >= 1")
    List<Patrimonio> findPatrimoniosWhereItemPatrimonioAvailable();

    List<Patrimonio> findAllByNomeContainsIgnoreCase(String nome);

}
