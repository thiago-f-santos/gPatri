package br.edu.ifg.numbers.msusuarios.config;

import br.edu.ifg.numbers.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.msusuarios.repository.CargoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CargosIniciais implements CommandLineRunner {

    private final CargoRepository cargoRepository;

    public CargosIniciais(CargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
    }

    @Override
    public void run(String... args) throws Exception{
        if (cargoRepository.findByNome("COORDENADOR_LABORATORIO").isEmpty()) {
            cargoRepository.save(new Cargo("COORDENADOR_LABORATORIO"));
        }
        if (cargoRepository.findByNome("GERENTE_PATRIMONIO").isEmpty()) {
            cargoRepository.save(new Cargo("GERENTE_PATRIMONIO"));
        }
        if (cargoRepository.findByNome("USUARIO_COMUM").isEmpty()) {
            cargoRepository.save(new Cargo("USUARIO_COMUM"));
        }

        System.out.println("Cargos iniciais verificados/inseridos no banco de dados.");
    }
}
