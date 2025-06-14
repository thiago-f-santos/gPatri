package br.edu.ifg.numbers.gpatri.msusuarios.repository;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CargoRepository extends JpaRepository<Cargo, UUID> {

    Optional<Cargo> findByNome(String cargo);
}
