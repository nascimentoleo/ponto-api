package com.example.ponto.api.controllers;

import com.example.ponto.api.dtos.LancamentoDto;
import com.example.ponto.api.entities.Funcionario;
import com.example.ponto.api.entities.Lancamento;
import com.example.ponto.api.enums.TipoEnum;
import com.example.ponto.api.response.Response;
import com.example.ponto.api.services.FuncionarioService;
import com.example.ponto.api.services.LancamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@CrossOrigin("*")
public class LancamentoController {

  private static final Logger log = LoggerFactory.getLogger(LancamentoController.class);
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Autowired private LancamentoService lancamentoService;
  @Autowired private FuncionarioService funcionarioService;

  @Value("${paginacao.qtd_por_pagina}")
  private int qtdPorPagina;

  public LancamentoController() {}

  @GetMapping("/{funcionarioId}")
  public ResponseEntity<Response<Page<LancamentoDto>>> listaPorFuncionarioId(
      @PathVariable("funcionarioId") final Long funcionarioId,
      @RequestParam(value = "pag", defaultValue = "0") final int pag,
      @RequestParam(value = "ord", defaultValue = "id") final String ord,
      @RequestParam(value = "dir", defaultValue = "DESC") final String dir) {
    log.info("Buscando lancamentos por Id do funcionario: {}, página {}", funcionarioId, pag);
    final Response<Page<LancamentoDto>> response = new Response<>();
    final PageRequest pageRequest =
        PageRequest.of(pag, this.qtdPorPagina, Sort.Direction.valueOf(dir));
    final Page<Lancamento> lancamentos =
        lancamentoService.buscarPorFuncionarioId(funcionarioId, pageRequest);
    final Page<LancamentoDto> lancamentoDtos = lancamentos.map(this::converterLancamentoDto);
    response.setData(lancamentoDtos);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Response<LancamentoDto>> listarPorId(@PathVariable("id") final Long id) {
    log.info("Buscando lancamentos por Id: {}", id);
    final Response<LancamentoDto> response = new Response<>();
    final Optional<Lancamento> lancamento = lancamentoService.buscarPorId(id);
    if (!lancamento.isPresent()) {
      log.info("Lançamento não encontrado para o id: {}", id);
      response.getErrors().add("Lançamento não encontrado para o id" + id);
      return ResponseEntity.badRequest().body(response);
    }
    response.setData(this.converterLancamentoDto(lancamento.get()));
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<Response<LancamentoDto>> adicionar(
      @RequestBody final LancamentoDto lancamentoDto, final BindingResult result)
      throws ParseException {
    log.info("Adicionando lancamento: {}", lancamentoDto);
    final Response<LancamentoDto> response = new Response<>();
    this.validarFuncionario(lancamentoDto, result);
    final Lancamento lancamento = this.converterDtoParaLancamento(lancamentoDto, result);

    if (result.hasErrors()) {
      log.error("Erro validando lancamento: {}", result.getAllErrors());
      result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
      return ResponseEntity.badRequest().body(response);
    }
    response.setData(converterLancamentoDto(this.lancamentoService.persistir(lancamento)));
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Response<LancamentoDto>> atualizar(
      @PathVariable("id") final Long id,
      @Valid @RequestBody final LancamentoDto lancamentoDto,
      final BindingResult result)
      throws ParseException {
    log.info("Atualizando lancamento: {}", lancamentoDto);
    final Response<LancamentoDto> response = new Response<>();
    this.validarFuncionario(lancamentoDto, result);
    lancamentoDto.setId(Optional.of(id));
    final Lancamento lancamento = this.converterDtoParaLancamento(lancamentoDto, result);

    if (result.hasErrors()) {
      log.error("Erro validando lancamento: {}", result.getAllErrors());
      result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
      return ResponseEntity.badRequest().body(response);
    }
    response.setData(converterLancamentoDto(this.lancamentoService.persistir(lancamento)));
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Response<String>> remover(@PathVariable("id") final Long id) {
    log.info("Removendo lancamento: {}", id);
    final Response<String> response = new Response<>();

    final Optional<Lancamento> lancamento = lancamentoService.buscarPorId(id);

    if (!lancamento.isPresent()) {
      log.error("Erro ao remover lancamento: {}, Registro não encontrado", id);
      response
          .getErrors()
          .add(String.format("Erro ao remover lancamento: %s, Registro não encontrado", id));
      return ResponseEntity.badRequest().body(response);
    }
    lancamentoService.remover(id);
    return ResponseEntity.ok(response);
  }

  private Lancamento converterDtoParaLancamento(
      final LancamentoDto lancamentoDto, final BindingResult result) throws ParseException {
    final Lancamento lancamento = new Lancamento();
    lancamento.setData(dateFormat.parse(lancamentoDto.getData()));
    lancamento.setTipo(TipoEnum.valueOf(lancamentoDto.getTipo()));
    lancamento.setDescricao(lancamentoDto.getDescricao());
    lancamento.setLocalizacao(lancamentoDto.getLocalizacao());
    final Optional<Funcionario> funcionario =
        funcionarioService.buscarPorId(lancamentoDto.getFuncionarioId());
    funcionario.ifPresent(lancamento::setFuncionario);
    return lancamento;
  }

  private void validarFuncionario(final LancamentoDto lancamentoDto, final BindingResult result) {
    if (lancamentoDto.getFuncionarioId() == null) {
      result.addError(new ObjectError("funcionario", "Funcionario nao informado"));
      return;
    }
    log.info("Validando funcionario: {}", lancamentoDto.getFuncionarioId());
    final Optional<Funcionario> funcionario =
        funcionarioService.buscarPorId(lancamentoDto.getFuncionarioId());
    if (!funcionario.isPresent()) {
      result.addError(new ObjectError("funcionario", "Funcionario não encontrado"));
    }
  }

  private LancamentoDto converterLancamentoDto(final Lancamento lancamento) {
    final LancamentoDto lancamentoDto = new LancamentoDto();
    lancamentoDto.setId(Optional.of(lancamento.getId()));
    lancamentoDto.setData(dateFormat.format(lancamento.getData()));
    lancamentoDto.setTipo(lancamento.getTipo().toString());
    lancamentoDto.setDescricao(lancamento.getDescricao());
    lancamentoDto.setLocalizacao(lancamento.getLocalizacao());
    lancamentoDto.setFuncionarioId(lancamento.getFuncionario().getId());
    return lancamentoDto;
  }
}
