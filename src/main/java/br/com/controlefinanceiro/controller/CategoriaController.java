
package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.domain.Categoria;
import br.com.controlefinanceiro.domain.TipoLancamento;
import br.com.controlefinanceiro.dto.CategoriaRequest;
import br.com.controlefinanceiro.dto.CategoriaResponse;
import br.com.controlefinanceiro.service.CategoriaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    // Cadastrar categoria
    @PostMapping
    public ResponseEntity<CategoriaResponse> criar(
            @Valid @RequestBody CategoriaRequest request) {

        Categoria categoria = service.criar(
                request.nome(),
                request.tipo()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(categoria));
    }

    // Listar categorias
    @GetMapping
    public List<CategoriaResponse> listar() {

        return service.listar()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Buscar categoria pelo ID
    @GetMapping("/{id}")
    public CategoriaResponse buscarPorId(
            @PathVariable Long id) {

        return toResponse(service.buscarPorId(id));
    }

    // Listar categorias por tipo
    @GetMapping("/tipo/{tipo}")
    public List<CategoriaResponse> listarPorTipo(
            @PathVariable TipoLancamento tipo) {

        return service.listarPorTipo(tipo)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Converter entidade em DTO
    private CategoriaResponse toResponse(Categoria categoria) {

        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getTipo()
        );
    }
}
