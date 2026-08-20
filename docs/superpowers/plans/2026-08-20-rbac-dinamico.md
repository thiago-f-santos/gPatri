# Plano de Implementação: Refatoração do Sistema de Cargos e Permissões (RBAC Dinâmico)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Refatorar o sistema de cargos e permissões do microsserviço `ms-usuarios` para RBAC dinâmico no banco de dados, permitindo gerenciamento dinâmico de cargos pelo administrador e consulta ao catálogo de permissões, mantendo compatibilidade com tokens JWT e os demais serviços.

**Architecture:** A entidade `Permissao` e o relacionamento N:N em `cargo_permissoes` substituem o `PermissaoEnum` estático. A aplicação disponibiliza endpoints para consulta do catálogo de permissões (`/api/v1/permissoes`) e CRUD dinâmico de cargos associando permissões por ID (`/api/v1/cargos`). A claim `permissoes` no JWT continua sendo preenchida como lista de strings para manter retrocompatibilidade stateless total com `ms-patrimonio` e `ms-gateway`.

**Tech Stack:** Java 25, Spring Boot 3, Spring Security, Spring Data JPA, PostgreSQL / Flyway, MapStruct, Lombok, JUnit 5, Mockito.

**Spec:** `docs/superpowers/specs/2026-08-20-rbac-dinamico-design.md`

## Global Constraints

- Java SDK: Amazon Corretto 25 / OpenJDK 25 via `export JAVA_HOME=/home/aluno/.sdkman/candidates/java/current && export PATH=$JAVA_HOME/bin:$PATH`
- Build / Test runner: `./mvnw -pl ms-usuarios test` (ou `./mvnw test` a partir da raiz)
- Formato de claims JWT: claim `"permissoes"` delimitada por vírgula no token (`"USUARIO_LISTAR,CARGO_CADASTRAR,PERMISSAO_LISTAR,..."`)
- Validação de entrada: Jakarta Validation (`@NotBlank`, `@NotEmpty`, `@Size`)
- Integridade relacional: Deletar cargo com usuários vinculados deve lançar `ConflictException` (409 Conflict)
- Permissões inexistentes em cadastro/edição de cargo devem lançar `BadRequestException` (400 Bad Request)

---

### Task 1: Migração de Banco de Dados Flyway (V2)

**Files:**
- Create: `ms-usuarios/src/main/resources/db/migration/V2__criar_tabela_permissoes_e_refatorar_rbac.sql`
- Test: `ms-usuarios/src/test/resources/application-test.yml`

**Interfaces:**
- Produces: Tabelas `permissoes` e `cargo_permissoes` atualizadas no PostgreSQL/H2 com chaves estrangeiras e integridade referencial.

- [ ] **Step 1: Criar o arquivo de migração SQL Flyway V2**

Crie o arquivo `ms-usuarios/src/main/resources/db/migration/V2__criar_tabela_permissoes_e_refatorar_rbac.sql`:
```sql
CREATE TABLE IF NOT EXISTS permissoes (
    id UUID PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    categoria VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Inserir permissões existentes no catálogo
INSERT INTO permissoes (id, nome, descricao, categoria) VALUES
    (gen_random_uuid(), 'ACESSO_ADMIN', 'Acesso administrativo geral ao sistema', 'ADMIN'),
    (gen_random_uuid(), 'USUARIO_CADASTRAR', 'Cadastrar novos usuários', 'USUARIOS'),
    (gen_random_uuid(), 'USUARIO_EDITAR', 'Editar usuários existentes', 'USUARIOS'),
    (gen_random_uuid(), 'USUARIO_EXCLUIR', 'Excluir usuários', 'USUARIOS'),
    (gen_random_uuid(), 'USUARIO_LISTAR', 'Listar e visualizar usuários', 'USUARIOS'),
    (gen_random_uuid(), 'CARGO_CADASTRAR', 'Cadastrar novos cargos', 'CARGOS'),
    (gen_random_uuid(), 'CARGO_EDITAR', 'Editar cargos e suas permissões', 'CARGOS'),
    (gen_random_uuid(), 'CARGO_EXCLUIR', 'Excluir cargos', 'CARGOS'),
    (gen_random_uuid(), 'CARGO_LISTAR', 'Listar e visualizar cargos', 'CARGOS'),
    (gen_random_uuid(), 'CARGO_ATRIBUIR', 'Atribuir cargo a usuários', 'CARGOS'),
    (gen_random_uuid(), 'PERMISSAO_LISTAR', 'Listar catálogo de permissões do sistema', 'PERMISSOES'),
    (gen_random_uuid(), 'EMPRESTIMO_SOLICITAR', 'Solicitar novo empréstimo', 'EMPRESTIMOS'),
    (gen_random_uuid(), 'EMPRESTIMO_EDITAR', 'Editar empréstimos próprios', 'EMPRESTIMOS'),
    (gen_random_uuid(), 'EMPRESTIMO_EXCLUIR', 'Excluir empréstimos próprios', 'EMPRESTIMOS'),
    (gen_random_uuid(), 'EMPRESTIMO_LISTAR', 'Listar empréstimos próprios', 'EMPRESTIMOS'),
    (gen_random_uuid(), 'EMPRESTIMO_LIBERAR', 'Aprovar e liberar empréstimos', 'EMPRESTIMOS'),
    (gen_random_uuid(), 'EMPRESTIMO_LISTAR_TODOS', 'Listar todos os empréstimos do sistema', 'EMPRESTIMOS'),
    (gen_random_uuid(), 'EMPRESTIMO_EDITAR_TODOS', 'Editar qualquer empréstimo do sistema', 'EMPRESTIMOS'),
    (gen_random_uuid(), 'EMPRESTIMO_EXCLUIR_TODOS', 'Excluir qualquer empréstimo do sistema', 'EMPRESTIMOS'),
    (gen_random_uuid(), 'ITEM_PATRIMONIO_CADASTRAR', 'Cadastrar novos itens de patrimônio', 'ITENS_PATRIMONIO'),
    (gen_random_uuid(), 'ITEM_PATRIMONIO_EDITAR', 'Editar itens de patrimônio', 'ITENS_PATRIMONIO'),
    (gen_random_uuid(), 'ITEM_PATRIMONIO_EXCLUIR', 'Excluir itens de patrimônio', 'ITENS_PATRIMONIO'),
    (gen_random_uuid(), 'ITEM_PATRIMONIO_LISTAR', 'Listar itens de patrimônio', 'ITENS_PATRIMONIO'),
    (gen_random_uuid(), 'PATRIMONIO_CADASTRAR', 'Cadastrar novos patrimônios', 'PATRIMONIO'),
    (gen_random_uuid(), 'PATRIMONIO_EDITAR', 'Editar patrimônios', 'PATRIMONIO'),
    (gen_random_uuid(), 'PATRIMONIO_EXCLUIR', 'Excluir patrimônios', 'PATRIMONIO'),
    (gen_random_uuid(), 'PATRIMONIO_LISTAR', 'Listar patrimônios', 'PATRIMONIO'),
    (gen_random_uuid(), 'CATEGORIA_CADASTRAR', 'Cadastrar categorias de patrimônio', 'CATEGORIAS'),
    (gen_random_uuid(), 'CATEGORIA_EDITAR', 'Editar categorias de patrimônio', 'CATEGORIAS'),
    (gen_random_uuid(), 'CATEGORIA_EXCLUIR', 'Excluir categorias de patrimônio', 'CATEGORIAS'),
    (gen_random_uuid(), 'CATEGORIA_LISTAR', 'Listar categorias de patrimônio', 'CATEGORIAS')
ON CONFLICT (nome) DO NOTHING;

-- Criar tabela temporária para migrar cargo_permissoes caso exista tabela legada com coluna string
CREATE TABLE IF NOT EXISTS cargo_permissoes_new (
    cargo_id UUID NOT NULL,
    permissao_id UUID NOT NULL,
    PRIMARY KEY (cargo_id, permissao_id),
    CONSTRAINT fk_cargo_permissoes_cargo FOREIGN KEY (cargo_id) REFERENCES cargos(id) ON DELETE CASCADE,
    CONSTRAINT fk_cargo_permissoes_permissao FOREIGN KEY (permissao_id) REFERENCES permissoes(id) ON DELETE CASCADE
);

-- Se cargo_permissoes antiga existir e tiver coluna 'permissao', migrar dados
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'cargo_permissoes' AND column_name = 'permissao'
    ) THEN
        INSERT INTO cargo_permissoes_new (cargo_id, permissao_id)
        SELECT cp.cargo_id, p.id
        FROM cargo_permissoes cp
        JOIN permissoes p ON p.nome = cp.permissao
        ON CONFLICT DO NOTHING;

        DROP TABLE cargo_permissoes;
    END IF;
END $$;

ALTER TABLE IF EXISTS cargo_permissoes_new RENAME TO cargo_permissoes;
```

- [ ] **Step 2: Verificar sintaxe da migração e compilar**

Run: `export JAVA_HOME=/home/aluno/.sdkman/candidates/java/current && export PATH=$JAVA_HOME/bin:$PATH && ./mvnw -pl ms-usuarios test-compile`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit da migração Flyway**

```bash
git add ms-usuarios/src/main/resources/db/migration/V2__criar_tabela_permissoes_e_refatorar_rbac.sql
git commit -m "feat(ms-usuarios): adicionar migracao flyway V2 para permissoes e rbac dinamico"
```

---

### Task 2: Entidades JPA do Domínio (`Permissao`, `Cargo`, `Usuario`)

**Files:**
- Create: `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/domain/Permissao.java`
- Modify: `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/domain/Cargo.java`
- Modify: `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/domain/Usuario.java`
- Delete: `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/domain/enums/PermissaoEnum.java`

**Interfaces:**
- Produces: `Permissao` entity com `id`, `nome`, `descricao`, `categoria`; `Cargo.getPermissoes()` retornando `Set<Permissao>`; `Usuario.getAuthorities()` retornando `SimpleGrantedAuthority(p.getNome())`.

- [ ] **Step 1: Criar a entidade `Permissao`**

Crie `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/domain/Permissao.java`:
```java
package br.edu.ifg.numbers.gpatri.msusuarios.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "permissoes")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Permissao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Column(length = 100)
    private String categoria;

    public Permissao(String nome, String descricao, String categoria) {
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
    }
}
```

- [ ] **Step 2: Atualizar a entidade `Cargo` com `@ManyToMany`**

Modifique `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/domain/Cargo.java`:
```java
package br.edu.ifg.numbers.gpatri.msusuarios.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "cargos")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cargo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "cargo_permissoes",
            joinColumns = @JoinColumn(name = "cargo_id"),
            inverseJoinColumns = @JoinColumn(name = "permissao_id")
    )
    private Set<Permissao> permissoes = new HashSet<>();

    public Cargo(String nome) {
        this.nome = nome;
        this.permissoes = new HashSet<>();
    }

    public Cargo(String nome, Set<Permissao> permissoes) {
        this.nome = nome;
        this.permissoes = permissoes != null ? permissoes : new HashSet<>();
    }
}
```

- [ ] **Step 3: Atualizar a entidade `Usuario` com `getAuthorities()` dinâmico**

Modifique `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/domain/Usuario.java` para converter `Permissao.getNome()` em `SimpleGrantedAuthority`:
```java
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.cargo != null && this.cargo.getPermissoes() != null) {
            return this.cargo.getPermissoes().stream()
                    .map(permissao -> new SimpleGrantedAuthority(permissao.getNome()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
```

- [ ] **Step 4: Remover `PermissaoEnum.java` e compilar**

Remova `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/domain/enums/PermissaoEnum.java`.

- [ ] **Step 5: Commit das alterações de domínio**

```bash
git add ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/domain/
git commit -m "refactor(ms-usuarios): atualizar entidades Cargo e Usuario para modelo dinâmico com Permissao"
```

---

### Task 3: Repositories, DTOs e Mappers para Permissão e Cargo

**Files:**
- Create: `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/repository/PermissaoRepository.java`
- Create: `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/dto/PermissaoResponseDTO.java`
- Modify: `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/dto/CargoRequestDTO.java`
- Modify: `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/dto/CargoResponseDTO.java`
- Create: `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/mapper/PermissaoMapper.java`
- Modify: `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/mapper/CargoMapper.java`

**Interfaces:**
- Produces:
  - `PermissaoRepository` (`findByNome(String)`, `findAllByOrderByCategoriaAscNomeAsc()`)
  - `PermissaoResponseDTO` (`UUID id`, `String nome`, `String descricao`, `String categoria`)
  - `CargoRequestDTO` (`String nome`, `Set<UUID> permissoesIds`)
  - `CargoResponseDTO` (`UUID id`, `String nome`, `Set<PermissaoResponseDTO> permissoes`)

- [ ] **Step 1: Criar `PermissaoRepository`**

Crie `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/repository/PermissaoRepository.java`:
```java
package br.edu.ifg.numbers.gpatri.msusuarios.repository;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissaoRepository extends JpaRepository<Permissao, UUID> {
    Optional<Permissao> findByNome(String nome);
    List<Permissao> findAllByOrderByCategoriaAscNomeAsc();
    List<Permissao> findByCategoriaOrderByNomeAsc(String categoria);
}
```

- [ ] **Step 2: Criar `PermissaoResponseDTO` e atualizar DTOs de Cargo**

Crie `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/dto/PermissaoResponseDTO.java`:
```java
package br.edu.ifg.numbers.gpatri.msusuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissaoResponseDTO {
    private UUID id;
    private String nome;
    private String descricao;
    private String categoria;
}
```

Modifique `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/dto/CargoRequestDTO.java`:
```java
package br.edu.ifg.numbers.gpatri.msusuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CargoRequestDTO {

    @NotBlank(message = "O nome do cargo é obrigatório")
    @Size(min = 3, max = 50, message = "O nome do cargo deve ter entre 3 e 50 caracteres")
    private String nome;

    @NotEmpty(message = "As permissões são obrigatórias")
    private Set<UUID> permissoesIds;
}
```

Modifique `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/dto/CargoResponseDTO.java`:
```java
package br.edu.ifg.numbers.gpatri.msusuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CargoResponseDTO {
    private UUID id;
    private String nome;
    private Set<PermissaoResponseDTO> permissoes;
}
```

- [ ] **Step 3: Criar `PermissaoMapper` e atualizar `CargoMapper`**

Crie `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/mapper/PermissaoMapper.java`:
```java
package br.edu.ifg.numbers.gpatri.msusuarios.mapper;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Permissao;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.PermissaoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PermissaoMapper {
    PermissaoResponseDTO toDto(Permissao permissao);
    List<PermissaoResponseDTO> toDtoList(List<Permissao> permissoes);
    Set<PermissaoResponseDTO> toDtoSet(Set<Permissao> permissoes);
}
```

Modifique `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/mapper/CargoMapper.java`:
```java
package br.edu.ifg.numbers.gpatri.msusuarios.mapper;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {PermissaoMapper.class})
public interface CargoMapper {

    @Mapping(target = "id", source = "cargo.id")
    @Mapping(target = "nome", source = "cargo.nome")
    @Mapping(target = "permissoes", source = "cargo.permissoes")
    CargoResponseDTO toDto(Cargo cargo);

    List<CargoResponseDTO> toDtoList(List<Cargo> cargos);
}
```

- [ ] **Step 4: Commit dos repositórios, DTOs e Mappers**

```bash
git add ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/repository/PermissaoRepository.java \
        ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/dto/ \
        ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/mapper/
git commit -m "feat(ms-usuarios): adicionar repositorios, dtos e mappers para Permissao e Cargo"
```

---

### Task 4: Serviço e Controller de Permissões com Testes Unitários

**Files:**
- Create: `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/service/PermissaoService.java`
- Create: `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/controller/PermissaoController.java`
- Create: `ms-usuarios/src/test/java/br/edu/ifg/numbers/gpatri/msusuarios/service/PermissaoServiceTest.java`

**Interfaces:**
- Produces:
  - `PermissaoService.buscarTodas()` -> `List<PermissaoResponseDTO>`
  - `PermissaoService.findById(UUID)` -> `PermissaoResponseDTO`
  - `GET /api/v1/permissoes` protegido por `hasAuthority('PERMISSAO_LISTAR')`
  - `GET /api/v1/permissoes/{id}` protegido por `hasAuthority('PERMISSAO_LISTAR')`

- [ ] **Step 1: Escrever teste unitário `PermissaoServiceTest`**

Crie `ms-usuarios/src/test/java/br/edu/ifg/numbers/gpatri/msusuarios/service/PermissaoServiceTest.java`:
```java
package br.edu.ifg.numbers.gpatri.msusuarios.service;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Permissao;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.PermissaoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ResourceNotFoundException;
import br.edu.ifg.numbers.gpatri.msusuarios.mapper.PermissaoMapper;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.PermissaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissaoServiceTest {

    @Mock
    private PermissaoRepository permissaoRepository;

    @Mock
    private PermissaoMapper permissaoMapper;

    @InjectMocks
    private PermissaoService permissaoService;

    private Permissao permissao;
    private PermissaoResponseDTO permissaoResponseDTO;
    private UUID permissaoId;

    @BeforeEach
    void setUp() {
        permissaoId = UUID.randomUUID();
        permissao = Permissao.builder()
                .id(permissaoId)
                .nome("PATRIMONIO_LISTAR")
                .descricao("Listar patrimonios")
                .categoria("PATRIMONIO")
                .build();
        permissaoResponseDTO = PermissaoResponseDTO.builder()
                .id(permissaoId)
                .nome("PATRIMONIO_LISTAR")
                .descricao("Listar patrimonios")
                .categoria("PATRIMONIO")
                .build();
    }

    @Test
    @DisplayName("Deve buscar todas as permissões com sucesso")
    void deveBuscarTodasAsPermissoes() {
        when(permissaoRepository.findAllByOrderByCategoriaAscNomeAsc()).thenReturn(List.of(permissao));
        when(permissaoMapper.toDtoList(List.of(permissao))).thenReturn(List.of(permissaoResponseDTO));

        List<PermissaoResponseDTO> resultado = permissaoService.buscarTodas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("PATRIMONIO_LISTAR", resultado.get(0).getNome());
        verify(permissaoRepository).findAllByOrderByCategoriaAscNomeAsc();
    }

    @Test
    @DisplayName("Deve buscar permissão por ID com sucesso")
    void deveBuscarPermissaoPorId() {
        when(permissaoRepository.findById(permissaoId)).thenReturn(Optional.of(permissao));
        when(permissaoMapper.toDto(permissao)).thenReturn(permissaoResponseDTO);

        PermissaoResponseDTO resultado = permissaoService.findById(permissaoId);

        assertNotNull(resultado);
        assertEquals(permissaoId, resultado.getId());
        assertEquals("PATRIMONIO_LISTAR", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando permissão não for encontrada")
    void deveLancarExcecaoQuandoPermissaoNaoEncontrada() {
        when(permissaoRepository.findById(permissaoId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> permissaoService.findById(permissaoId));
    }
}
```

- [ ] **Step 2: Implementar `PermissaoService`**

Crie `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/service/PermissaoService.java`:
```java
package br.edu.ifg.numbers.gpatri.msusuarios.service;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Permissao;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.PermissaoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ResourceNotFoundException;
import br.edu.ifg.numbers.gpatri.msusuarios.mapper.PermissaoMapper;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.PermissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissaoService {

    private final PermissaoRepository permissaoRepository;
    private final PermissaoMapper permissaoMapper;

    @Transactional(readOnly = true)
    public List<PermissaoResponseDTO> buscarTodas() {
        List<Permissao> permissoes = permissaoRepository.findAllByOrderByCategoriaAscNomeAsc();
        return permissaoMapper.toDtoList(permissoes);
    }

    @Transactional(readOnly = true)
    public PermissaoResponseDTO findById(UUID id) {
        Permissao permissao = permissaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Permissão de ID '%s' não encontrada.", id)));
        return permissaoMapper.toDto(permissao);
    }

    @Transactional(readOnly = true)
    public List<PermissaoResponseDTO> buscarPorCategoria(String categoria) {
        List<Permissao> permissoes = permissaoRepository.findByCategoriaOrderByNomeAsc(categoria);
        return permissaoMapper.toDtoList(permissoes);
    }
}
```

- [ ] **Step 3: Implementar `PermissaoController`**

Crie `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/controller/PermissaoController.java`:
```java
package br.edu.ifg.numbers.gpatri.msusuarios.controller;

import br.edu.ifg.numbers.gpatri.msusuarios.dto.PermissaoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.service.PermissaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/permissoes")
@RequiredArgsConstructor
@Tag(name = "Permissões", description = "Endpoints para consulta do catálogo de permissões do sistema")
public class PermissaoController {

    private final PermissaoService permissaoService;

    @Operation(summary = "Retorna todas as permissões cadastradas no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de permissões retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado, usuário não possui permissão"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSAO_LISTAR')")
    public ResponseEntity<List<PermissaoResponseDTO>> buscarTodas(
            @RequestParam(required = false) String categoria
    ) {
        if (categoria != null && !categoria.isBlank()) {
            return ResponseEntity.ok(permissaoService.buscarPorCategoria(categoria));
        }
        return ResponseEntity.ok(permissaoService.buscarTodas());
    }

    @Operation(summary = "Retorna uma permissão por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissão encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Permissão não encontrada"),
            @ApiResponse(responseCode = "403", description = "Acesso negado, usuário não possui permissão"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSAO_LISTAR')")
    public ResponseEntity<PermissaoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(permissaoService.findById(id));
    }
}
```

- [ ] **Step 4: Executar testes de permissão**

Run: `export JAVA_HOME=/home/aluno/.sdkman/candidates/java/current && export PATH=$JAVA_HOME/bin:$PATH && ./mvnw -pl ms-usuarios test -Dtest=PermissaoServiceTest`
Expected: Tests run: 3, Failures: 0, Errors: 0

- [ ] **Step 5: Commit do serviço e controller de permissões**

```bash
git add ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/service/PermissaoService.java \
        ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/controller/PermissaoController.java \
        ms-usuarios/src/test/java/br/edu/ifg/numbers/gpatri/msusuarios/service/PermissaoServiceTest.java
git commit -m "feat(ms-usuarios): adicionar PermissaoService, PermissaoController e testes"
```

---

### Task 5: Refatoração do `CargoService`, `CargoController` e Testes de Cargos

**Files:**
- Modify: `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/service/CargoService.java`
- Modify: `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/controller/CargoController.java`
- Modify: `ms-usuarios/src/test/java/br/edu/ifg/numbers/gpatri/msusuarios/service/CargoServiceTest.java`

**Interfaces:**
- Produces:
  - `CargoService.criarCargo(CargoRequestDTO)` valida permissões no banco e salva
  - `CargoService.atualizarCargo(UUID, CargoRequestDTO)` atualiza nome e permissões
  - `CargoService.deletarCargo(UUID)`
  - `CargoService.findById(UUID)` e `buscarTodos()`

- [ ] **Step 1: Refatorar `CargoService` para validar IDs de permissão e associar entidades**

Atualize `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/service/CargoService.java`:
```java
package br.edu.ifg.numbers.gpatri.msusuarios.service;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.Permissao;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.BadRequestException;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ConflictException;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ResourceNotFoundException;
import br.edu.ifg.numbers.gpatri.msusuarios.mapper.CargoMapper;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.CargoRepository;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.PermissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CargoService {

    private final CargoRepository cargoRepository;
    private final PermissaoRepository permissaoRepository;
    private final CargoMapper cargoMapper;

    @Transactional
    public CargoResponseDTO criarCargo(CargoRequestDTO cargoRequestDTO) {
        if (cargoRepository.findByNome(cargoRequestDTO.getNome()).isPresent()) {
            throw new ConflictException(String.format("Já existe um cargo com o nome: %s", cargoRequestDTO.getNome()));
        }

        Set<Permissao> permissoes = buscarPermissoesValidadas(cargoRequestDTO.getPermissoesIds());

        Cargo cargo = Cargo.builder()
                .nome(cargoRequestDTO.getNome())
                .permissoes(permissoes)
                .build();

        cargo = cargoRepository.save(cargo);
        return cargoMapper.toDto(cargo);
    }

    @Transactional(readOnly = true)
    public CargoResponseDTO findById(UUID id) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Cargo de ID '%s' não encontrado.", id)));
        return cargoMapper.toDto(cargo);
    }

    @Transactional(readOnly = true)
    public CargoResponseDTO buscarPorNome(String nome) {
        Cargo cargo = cargoRepository.findByNome(nome)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Cargo de nome '%s' não encontrado.", nome)));
        return cargoMapper.toDto(cargo);
    }

    @Transactional(readOnly = true)
    public List<CargoResponseDTO> buscarTodos() {
        List<Cargo> cargos = cargoRepository.findAll();
        return cargoMapper.toDtoList(cargos);
    }

    @Transactional
    public CargoResponseDTO atualizarCargo(UUID id, CargoRequestDTO cargoRequestDTO) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Cargo de ID '%s' não encontrado.", id)));

        if (!cargo.getNome().equals(cargoRequestDTO.getNome()) && cargoRepository.findByNome(cargoRequestDTO.getNome()).isPresent()) {
            throw new ConflictException(String.format("Já existe um cargo com o nome: %s", cargoRequestDTO.getNome()));
        }

        Set<Permissao> novasPermissoes = buscarPermissoesValidadas(cargoRequestDTO.getPermissoesIds());

        cargo.setNome(cargoRequestDTO.getNome());
        cargo.setPermissoes(novasPermissoes);
        cargo = cargoRepository.save(cargo);

        return cargoMapper.toDto(cargo);
    }

    @Transactional
    public void deletarCargo(UUID id) {
        if (!cargoRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("Cargo de ID '%s' não encontrado.", id));
        }
        try {
            cargoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(String.format("Não é possivel deletar o cargo de ID '%s' pois ele está " +
                    "vinculado a um ou mais usuários. Antes de deletar, remova os usuários vinculados a este cargo.", id));
        }
    }

    private Set<Permissao> buscarPermissoesValidadas(Set<UUID> permissoesIds) {
        if (permissoesIds == null || permissoesIds.isEmpty()) {
            throw new BadRequestException("Pelo menos uma permissão deve ser informada para o cargo.");
        }

        List<Permissao> permissoesEncontradas = permissaoRepository.findAllById(permissoesIds);

        if (permissoesEncontradas.size() != permissoesIds.size()) {
            Set<UUID> idsEncontrados = permissoesEncontradas.stream().map(Permissao::getId).collect(Collectors.toSet());
            Set<UUID> idsFaltantes = permissoesIds.stream()
                    .filter(id -> !idsEncontrados.contains(id))
                    .collect(Collectors.toSet());

            throw new BadRequestException(String.format("As seguintes permissões não foram encontradas no sistema: %s", idsFaltantes));
        }

        return new HashSet<>(permissoesEncontradas);
    }
}
```

- [ ] **Step 2: Atualizar testes unitários em `CargoServiceTest`**

Reescreva `ms-usuarios/src/test/java/br/edu/ifg/numbers/gpatri/msusuarios/service/CargoServiceTest.java` com cobertura completa:
- Criar cargo com permissões válidas
- Lançar `ConflictException` se nome já existir
- Lançar `BadRequestException` se ID de permissão não existir
- Atualizar cargo com sucesso
- Deletar cargo com sucesso e lançar `ConflictException` em caso de `DataIntegrityViolationException`

- [ ] **Step 3: Executar os testes de `CargoService`**

Run: `export JAVA_HOME=/home/aluno/.sdkman/candidates/java/current && export PATH=$JAVA_HOME/bin:$PATH && ./mvnw -pl ms-usuarios test -Dtest=CargoServiceTest`
Expected: BUILD SUCCESS, all tests passing.

- [ ] **Step 4: Commit de `CargoService` e `CargoController`**

```bash
git add ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/service/CargoService.java \
        ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/controller/CargoController.java \
        ms-usuarios/src/test/java/br/edu/ifg/numbers/gpatri/msusuarios/service/CargoServiceTest.java
git commit -m "refactor(ms-usuarios): refatorar CargoService e CargoController para gerenciamento dinamico de permissoes"
```

---

### Task 6: Atualizar `InitialSetupConfig`, `AuthService` e Suite Completa de Testes

**Files:**
- Modify: `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/config/InitialSetupConfig.java`
- Modify: `ms-usuarios/src/test/java/br/edu/ifg/numbers/gpatri/msusuarios/service/AuthServiceTest.java`
- Modify: `ms-usuarios/src/test/java/br/edu/ifg/numbers/gpatri/msusuarios/service/UsuarioServiceTest.java`

**Interfaces:**
- Produces: `InitialSetupConfig` inserindo permissões dinâmicas no startup se banco estiver vazio, associando todas as permissões ao cargo "Administrador" e subconjunto ao cargo "Usuário"; todos os testes da aplicação passando.

- [ ] **Step 1: Atualizar `InitialSetupConfig`**

Modifique `ms-usuarios/src/main/java/br/edu/ifg/numbers/gpatri/msusuarios/config/InitialSetupConfig.java`:
```java
package br.edu.ifg.numbers.gpatri.msusuarios.config;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.Permissao;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.Usuario;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.CargoRepository;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.PermissaoRepository;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class InitialSetupConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CargoRepository cargoRepository;
    private final PermissaoRepository permissaoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        inicializarPermissoes();
        cargoAdmin();
        cargoUsuario();

        Optional<Cargo> admin = cargoRepository.findByNome("Administrador");

        if (admin.isPresent()) {
            Cargo cargoAdmin = admin.get();

            Optional<Usuario> adminUser = userRepository.findByEmail("admin@admin.com");

            if (adminUser.isEmpty()) {
                Usuario adminUsuario = new Usuario();
                adminUsuario.setNome("Administrador");
                adminUsuario.setSobrenome("Geral");
                adminUsuario.setEmail("admin@admin.com");
                adminUsuario.setSenha(passwordEncoder.encode("admin123"));
                adminUsuario.setCargo(cargoAdmin);

                userRepository.save(adminUsuario);

                log.info("ADMIN criado com sucesso");
                log.info("Email: admin@admin.com");
                log.info("Senha: admin123");
            } else {
                log.info("Usuário ADMIN já existe.");
            }
        } else {
            log.error("Cargo ADMIN não encontrado. Certifique-se de que o cargo ADMIN foi criado antes de iniciar a aplicação.");
        }
    }

    private void inicializarPermissoes() {
        if (permissaoRepository.count() == 0) {
            log.info("Catálogo de permissões vazio. Inicializando permissões padrão...");
            List<Permissao> lista = List.of(
                    new Permissao("ACESSO_ADMIN", "Acesso administrativo geral", "ADMIN"),
                    new Permissao("USUARIO_CADASTRAR", "Cadastrar novos usuários", "USUARIOS"),
                    new Permissao("USUARIO_EDITAR", "Editar usuários", "USUARIOS"),
                    new Permissao("USUARIO_EXCLUIR", "Excluir usuários", "USUARIOS"),
                    new Permissao("USUARIO_LISTAR", "Listar usuários", "USUARIOS"),
                    new Permissao("CARGO_CADASTRAR", "Cadastrar novos cargos", "CARGOS"),
                    new Permissao("CARGO_EDITAR", "Editar cargos", "CARGOS"),
                    new Permissao("CARGO_EXCLUIR", "Excluir cargos", "CARGOS"),
                    new Permissao("CARGO_LISTAR", "Listar cargos", "CARGOS"),
                    new Permissao("CARGO_ATRIBUIR", "Atribuir cargo a usuários", "CARGOS"),
                    new Permissao("PERMISSAO_LISTAR", "Listar permissões", "PERMISSOES"),
                    new Permissao("EMPRESTIMO_SOLICITAR", "Solicitar empréstimo", "EMPRESTIMOS"),
                    new Permissao("EMPRESTIMO_EDITAR", "Editar empréstimo", "EMPRESTIMOS"),
                    new Permissao("EMPRESTIMO_EXCLUIR", "Excluir empréstimo", "EMPRESTIMOS"),
                    new Permissao("EMPRESTIMO_LISTAR", "Listar empréstimos", "EMPRESTIMOS"),
                    new Permissao("EMPRESTIMO_LIBERAR", "Liberar empréstimos", "EMPRESTIMOS"),
                    new Permissao("EMPRESTIMO_LISTAR_TODOS", "Listar todos os empréstimos", "EMPRESTIMOS"),
                    new Permissao("EMPRESTIMO_EDITAR_TODOS", "Editar todos os empréstimos", "EMPRESTIMOS"),
                    new Permissao("EMPRESTIMO_EXCLUIR_TODOS", "Excluir todos os empréstimos", "EMPRESTIMOS"),
                    new Permissao("ITEM_PATRIMONIO_CADASTRAR", "Cadastrar item de patrimônio", "ITENS_PATRIMONIO"),
                    new Permissao("ITEM_PATRIMONIO_EDITAR", "Editar item de patrimônio", "ITENS_PATRIMONIO"),
                    new Permissao("ITEM_PATRIMONIO_EXCLUIR", "Excluir item de patrimônio", "ITENS_PATRIMONIO"),
                    new Permissao("ITEM_PATRIMONIO_LISTAR", "Listar itens de patrimônio", "ITENS_PATRIMONIO"),
                    new Permissao("PATRIMONIO_CADASTRAR", "Cadastrar patrimônio", "PATRIMONIO"),
                    new Permissao("PATRIMONIO_EDITAR", "Editar patrimônio", "PATRIMONIO"),
                    new Permissao("PATRIMONIO_EXCLUIR", "Excluir patrimônio", "PATRIMONIO"),
                    new Permissao("PATRIMONIO_LISTAR", "Listar patrimônios", "PATRIMONIO"),
                    new Permissao("CATEGORIA_CADASTRAR", "Cadastrar categoria", "CATEGORIAS"),
                    new Permissao("CATEGORIA_EDITAR", "Editar categoria", "CATEGORIAS"),
                    new Permissao("CATEGORIA_EXCLUIR", "Excluir categoria", "CATEGORIAS"),
                    new Permissao("CATEGORIA_LISTAR", "Listar categorias", "CATEGORIAS")
            );
            permissaoRepository.saveAll(lista);
            log.info("Permissões padrão inicializadas com sucesso.");
        }
    }

    private void cargoAdmin() {
        if (cargoRepository.findByNome("Administrador").isEmpty()) {
            List<Permissao> todasPermissoes = permissaoRepository.findAll();
            Cargo cargo = new Cargo("Administrador", new HashSet<>(todasPermissoes));
            cargoRepository.save(cargo);
            log.info("Cargo Administrador criado com sucesso com todas as permissões.");
        } else {
            log.info("Cargo admin já existe.");
        }
    }

    private void cargoUsuario() {
        if (cargoRepository.findByNome("Usuário").isEmpty()) {
            Set<String> nomesPermissoes = Set.of(
                    "USUARIO_EDITAR", "EMPRESTIMO_EDITAR", "EMPRESTIMO_EXCLUIR",
                    "EMPRESTIMO_LISTAR", "EMPRESTIMO_SOLICITAR", "PATRIMONIO_LISTAR",
                    "ITEM_PATRIMONIO_LISTAR", "CATEGORIA_LISTAR", "USUARIO_LISTAR"
            );
            List<Permissao> todas = permissaoRepository.findAll();
            Set<Permissao> permissoesUsuario = todas.stream()
                    .filter(p -> nomesPermissoes.contains(p.getNome()))
                    .collect(Collectors.toSet());

            Cargo cargo = new Cargo("Usuário", permissoesUsuario);
            cargoRepository.save(cargo);
            log.info("Cargo Usuário criado com sucesso.");
        } else {
            log.info("Cargo usuário já existe.");
        }
    }
}
```

- [ ] **Step 2: Atualizar testes `AuthServiceTest` e `UsuarioServiceTest`**

Atualize as instâncias mock de `Cargo` e `Permissao` nos testes existentes para utilizar o novo modelo de objetos `Permissao` em vez do enum.

- [ ] **Step 3: Executar toda a suíte de testes de `ms-usuarios`**

Run: `export JAVA_HOME=/home/aluno/.sdkman/candidates/java/current && export PATH=$JAVA_HOME/bin:$PATH && ./mvnw -pl ms-usuarios test`
Expected: BUILD SUCCESS com todos os testes passando.

- [ ] **Step 4: Commit final e verificação geral**

```bash
git add ms-usuarios/src/
git commit -m "feat(ms-usuarios): atualizar InitialSetupConfig e testes para modelo dinamico de permissoes"
```
