package com.example.ponto.api.controllers;

import com.example.ponto.api.dtos.CadastroPjDto;
import com.example.ponto.api.entities.Empresa;
import com.example.ponto.api.entities.Funcionario;
import com.example.ponto.api.response.Response;
import com.example.ponto.api.services.EmpresaService;
import com.example.ponto.api.services.FuncionarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/cadastrar-pj")
@CrossOrigin(origins = "*")
public class CadastroPjController {

    private static final Logger log = LoggerFactory.getLogger(CadastroPjController.class);

    @Autowired
    private FuncionarioService funcionarioService;
    @Autowired
    private EmpresaService empresaService;

    public CadastroPjController() {
    }

    @PostMapping
    public ResponseEntity<Response<CadastroPjDto>> cadastrar(@Valid @RequestBody CadastroPjDto cadastroPjDto, BindingResult result) {
        log.info("Cadastrando PJ: {}", cadastroPjDto.toString());
        Response<CadastroPjDto> response = new Response<>();

        validarDadosExistentes(cadastroPjDto, result);
        Empresa empresa = extrairEmpresa(cadastroPjDto);
        Funcionario funcionario = extrairFuncionario(cadastroPjDto);

        if(result.hasErrors()) {
            log.error("Erro validando dados de cadastro PJ: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        empresaService.persistir(empresa);
        funcionario.setEmpresa(empresa);
        funcionarioService.persistir(funcionario);

        response.setData(converterCadastroPjDto(funcionario));
        return ResponseEntity.ok(response);
    }

    private CadastroPjDto converterCadastroPjDto(Funcionario funcionario) {
        return null;
    }

    private Funcionario extrairFuncionario(CadastroPjDto cadastroPjDto) {
        return null;
    }

    private Empresa extrairEmpresa(CadastroPjDto cadastroPjDto) {
        return null;
    }

    private void validarDadosExistentes(CadastroPjDto cadastroPjDto, BindingResult result) {
        empresaService.buscarPorCnpj(cadastroPjDto.getCnpj()).ifPresent(emp -> result.addError(new ObjectError("empresa", "Empresa já cadastrada")));
        funcionarioService.buscarPorCpf(cadastroPjDto.getCpf()).ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já cadastrado")));
        funcionarioService.buscarPorEmail(cadastroPjDto.getEmail()).ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já cadastrado")));
    }
}
