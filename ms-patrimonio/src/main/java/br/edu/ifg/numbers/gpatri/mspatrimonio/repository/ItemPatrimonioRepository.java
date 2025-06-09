package br.edu.ifg.numbers.gpatri.mspatrimonio.repository;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Condicao;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemPatrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Patrimonio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemPatrimonioRepository extends JpaRepository<ItemPatrimonio, UUID> {
    List<ItemPatrimonio> findAllByPatrimonio(Patrimonio patrimonio);

    List<ItemPatrimonio> findAllByCondicao(Condicao condicao);

    List<ItemPatrimonio> findAllByEmUsoEquals(boolean emUso);

    List<ItemPatrimonio> findAllByPatrimonioId(UUID patrimonioId);

    List<ItemPatrimonio> findAllByCondicaoId(UUID condicaoId);
}
