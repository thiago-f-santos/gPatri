package br.edu.ifg.numbers.gpatri.mspatrimonio.repository;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemEmprestimoRepository extends JpaRepository<ItemEmprestimo, UUID> {

    List<ItemEmprestimo> findAllByEmprestimo_IdEquals(UUID id);

    ItemEmprestimo findByEmprestimo_IdEqualsAndItemPatrimonio_IdEquals(UUID emprestimoId, UUID itemPatrimonioId);

}
