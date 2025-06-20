package br.edu.ifg.numbers.gpatri.mspatrimonio.service;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Categoria;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CategoriaCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CategoriaResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CategoriaUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.CategoriaMapper;
import br.edu.ifg.numbers.gpatri.mspatrimonio.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Transactional
    public CategoriaResponseDTO save(CategoriaCreateDTO categoriaCreateDTO) {
        Categoria categoria = categoriaMapper.createDtoToCategoria(categoriaCreateDTO);
        if (categoriaCreateDTO.getIdCategoriaMae() != null) {
            Categoria categoriaMae = categoriaRepository.findById(categoriaCreateDTO.getIdCategoriaMae()).orElseThrow(
                    () -> new EntityNotFoundException("Categoria mãe não encontrada."));
            categoria.setCategoriaMae(categoriaMae);
            categoriaMae.getSubcategorias().add(categoria);
        } else {
            categoria.setCategoriaMae(null);
        }
        categoria.setCreatedAt(Instant.now());
        categoria = categoriaRepository.save(categoria);
        return categoriaMapper.categoriaToCategoriaResponseDTO(categoria);
    }

    @Transactional
    public CategoriaResponseDTO update(UUID id, CategoriaUpdateDTO categoriaUpdateDTO) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Categoria a ser atualizada não foi encontrada"));
        if (categoriaUpdateDTO.getNome() != null) {
            categoria.setNome(categoriaUpdateDTO.getNome());
        }
        if (categoriaUpdateDTO.getIdCategoriaMae() != null) {
            if (categoria.getCategoriaMae() == null || !categoriaUpdateDTO.getIdCategoriaMae().equals(categoria.getCategoriaMae().getId()))  {
                if (categoria.getCategoriaMae() != null) {
                    categoria.getCategoriaMae().getSubcategorias().remove(categoria);
                }
            }
            Categoria novaCategoriaMae = categoriaRepository.findById(categoriaUpdateDTO.getIdCategoriaMae()).orElseThrow(
                    () -> new EntityNotFoundException("Nova categoria mãe não encontrada"));
            categoria.setCategoriaMae(novaCategoriaMae);
            novaCategoriaMae.getSubcategorias().add(categoria);
        } else if (categoria.getCategoriaMae() != null) {
            categoria.getCategoriaMae().getSubcategorias().remove(categoria);
            categoria.setCategoriaMae(null);
        }
        categoria.setUpdatedAt(Instant.now());
        categoria = categoriaRepository.save(categoria);
        return categoriaMapper.categoriaToCategoriaResponseDTO(categoria);
    }

    @Transactional
    public void deleteById(UUID id) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Categoria não foi encontrada"));
        if (categoria.getCategoriaMae() != null) {
            categoria.getCategoriaMae().getSubcategorias().remove(categoria);
        }
        categoriaRepository.delete(categoria);
    }

    public CategoriaResponseDTO findById(UUID id) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Categoria não foi encontrada"));
        return categoriaMapper.categoriaToCategoriaResponseDTO(categoria);
    }

    public List<CategoriaResponseDTO> findAll() {
        List<Categoria> categorias = categoriaRepository.findAll();
        return categorias.stream().map(categoriaMapper::categoriaToCategoriaResponseDTO).toList();
    }

}
