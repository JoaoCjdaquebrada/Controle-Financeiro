
package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.domain.ContaFinanceira;
import br.com.controlefinanceiro.dto.ContaRequest;
import br.com.controlefinanceiro.dto.ContaResponse;
import br.com.controlefinanceiro.service.ContaFinanceiraService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contas")
public class ContaFinanceiraController {

    private final ContaFinanceiraService service;

    public ContaFinanceiraController(
            ContaFinanceiraService service) {
        this.service = service;
    }

    // Cadastrar conta
    @PostMapping
    public ResponseEntity<ContaResponse> criar(
            @Valid @RequestBody ContaRequest request) {

        ContaFinanceira conta = service.criar(
                request.nome(),
                request.tipoConta(),
                request.saldoInicial()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(conta));
    }

    // Listar contas
    @GetMapping
    public List<ContaResponse> listar() {

        return service.listar()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Buscar conta pelo ID
    @GetMapping("/{id}")
    public ContaResponse buscarPorId(
            @PathVariable Long id) {

        ContaFinanceira conta = service.buscarPorId(id);

        return toResponse(conta);
    }

    // Excluir conta
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {

        service.excluir(id);
    }

    // Converter entidade em DTO
    private ContaResponse toResponse(ContaFinanceira conta) {

        return new ContaResponse(
                conta.getId(),
                conta.getNome(),
                conta.getTipoConta(),
                conta.getSaldoInicial()
        );
    }
}
