package br.edu.ifg.numbers.gpatri.mspatrimonio.repository;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemPatrimonio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemPatrimonioRepository extends JpaRepository<ItemPatrimonio, UUID> {
    List<ItemPatrimonio> findAllByPatrimonioId(UUID patrimonioId);
    List<ItemPatrimonio> findAllByPatrimonio_Categoria_NomeContainingIgnoreCase(String patrimonioCategoriaNome);
    List<ItemPatrimonio> findAllByPatrimonio_NomeContainingIgnoreCase(String nomePatrimonio);
}
