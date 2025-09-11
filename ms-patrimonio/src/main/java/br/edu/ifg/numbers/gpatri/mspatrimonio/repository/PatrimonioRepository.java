package br.edu.ifg.numbers.gpatri.mspatrimonio.repository;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Patrimonio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatrimonioRepository extends JpaRepository<Patrimonio, UUID> {

    @Query(value = "SELECT DISTINCT p FROM Patrimonio p JOIN FETCH p.itensPatrimonio ip WHERE ip.quantidade >= 1")
    Page<Patrimonio> findPatrimoniosWhereItemPatrimonioAvailable(Pageable pageable);

    Page<Patrimonio> findAllByNomeContainsIgnoreCase(String nome, Pageable pageable);

}
