package com.example.ponto.api.controllers;

import com.example.ponto.api.dtos.FuncionarioDto;
import com.example.ponto.api.entities.Funcionario;
import com.example.ponto.api.response.Response;
import com.example.ponto.api.services.FuncionarioService;
import com.example.ponto.api.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin("*")
public class FuncionarioController {

    private static final Logger log = LoggerFactory.getLogger(FuncionarioController.class);

    @Autowired
    private FuncionarioService funcionarioService;


    @PutMapping(value = "/{id}")
    public ResponseEntity<Response<FuncionarioDto>> atualizar(@PathVariable("id") final Long id,
                                                              @Valid @RequestBody final FuncionarioDto funcionarioDto,
                                                              final BindingResult result) {
        log.info("Atualizando funcionário: {}", funcionarioDto.toString());
        final Response<FuncionarioDto> response = new Response<>();

        final Optional<Funcionario> funcionario = funcionarioService.buscarPorId(id);
        if (!funcionario.isPresent()) {
            response.getErrors().add(new ObjectError("funcionario", "Funcionário não encontrado").getDefaultMessage());
            return ResponseEntity.badRequest().body(response);
        }

        atualizarDadosFuncionario(funcionario.get(), funcionarioDto, result);

        if (result.hasErrors()) {
            log.error("Erro validando funcionário: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        funcionarioService.persistir(funcionario.get());
        response.setData(converterFuncionarioDto(funcionario.get()));
        return ResponseEntity.ok(response);

    }

    private FuncionarioDto converterFuncionarioDto(final Funcionario funcionario) {
        final FuncionarioDto funcionarioDto = new FuncionarioDto();
        funcionarioDto.setId(funcionario.getId());
        funcionarioDto.setNome(funcionario.getNome());
        funcionarioDto.setEmail(funcionario.getEmail());
        funcionario.getQtdHorasAlmocoOpt()
                .ifPresent(qtdHorasAlmoco -> funcionarioDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
        funcionario.getQtdHorasTrabalhoDiaOpt()
                .ifPresent(qtdHorasTrabalhoDia -> funcionarioDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabalhoDia))));
        funcionario.getValorHoraOpt()
                .ifPresent(valorHora -> funcionarioDto.setValorHora(Optional.of(valorHora.toString())));
        return funcionarioDto;
    }

    private void atualizarDadosFuncionario(final Funcionario funcionario, final FuncionarioDto funcionarioDto, final BindingResult result) {
        funcionario.setNome(funcionarioDto.getNome());

        if (!funcionario.getEmail().equals(funcionarioDto.getEmail())) {
            funcionarioService.buscarPorEmail(funcionarioDto.getEmail())
                    .ifPresent(func -> new ObjectError("email", "Email já existente"));
            funcionario.setEmail(funcionarioDto.getEmail());
        }
        funcionario.setQtdHorasAlmoco(null);
        funcionarioDto.getQtdHorasAlmoco()
                .ifPresent(qtdHorasTrabDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabDia)));

        funcionario.setQtdHorasTrabalhoDia(null);
        funcionarioDto.getValorHora()
                .ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));

        funcionario.setValorHora(null);
        funcionarioDto.getValorHora().ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));

        if (funcionarioDto.getSenha().isPresent()) {
            funcionario.setSenha(PasswordUtils.gerarBCrypt(funcionarioDto.getSenha().get()));
        }
    }
}
