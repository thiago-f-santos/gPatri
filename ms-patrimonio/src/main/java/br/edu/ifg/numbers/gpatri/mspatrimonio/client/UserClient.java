package br.edu.ifg.numbers.gpatri.mspatrimonio.client;

import br.edu.ifg.numbers.gpatri.mspatrimonio.client.config.UserClientAuthInterceptor;
import br.edu.ifg.numbers.gpatri.mspatrimonio.client.dto.ExternalUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "ms-usuarios", path = "/api/v1/usuarios", configuration = UserClientAuthInterceptor.class)
public interface UserClient {

    @GetMapping(value = "/usuarios")
    List<ExternalUserDTO> getUsersByIdsList(@RequestParam List<UUID> id);

    @GetMapping("/{id}")
    ExternalUserDTO getUserById(@PathVariable UUID id);

}