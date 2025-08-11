package br.edu.ifg.numbers.gpatri.mspatrimonio.repository;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Emprestimo;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.SituacaoEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, UUID> {
    List<Emprestimo> findAllBySituacaoEquals(SituacaoEmprestimo situacaoEmprestimo);
}
