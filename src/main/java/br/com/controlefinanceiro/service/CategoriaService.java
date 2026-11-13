
package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.domain.Categoria;
import br.com.controlefinanceiro.domain.TipoLancamento;
import br.com.controlefinanceiro.repository.CategoriaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    // Cadastrar categoria
    public Categoria criar(String nome, TipoLancamento tipo) {

        validarDados(nome, tipo);

        String nomeNormalizado = nome.trim();

        if (repository.existsByNomeAndTipo(nomeNormalizado, tipo)) {
            throw new IllegalArgumentException(
                    "Essa categoria já está cadastrada."
            );
        }

        Categoria categoria = new Categoria(
                nomeNormalizado,
                tipo
        );

        return repository.save(categoria);
    }

    // Listar todas as categorias
    @Transactional(readOnly = true)
    public List<Categoria> listar() {

        return repository.findAll();
    }

    // Buscar categoria pelo ID
    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Categoria não encontrada."
                        )
                );
    }

    // Listar categorias por tipo
    @Transactional(readOnly = true)
    public List<Categoria> listarPorTipo(TipoLancamento tipo) {

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "O tipo da categoria é obrigatório."
            );
        }

        return repository.findByTipo(tipo);
    }

    // Validar os dados
    private void validarDados(String nome, TipoLancamento tipo) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome da categoria é obrigatório."
            );
        }

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "O tipo da categoria é obrigatório."
            );
        }
    }
}
