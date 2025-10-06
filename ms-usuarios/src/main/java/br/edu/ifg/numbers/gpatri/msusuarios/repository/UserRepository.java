package br.edu.ifg.numbers.gpatri.msusuarios.repository;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);
}
