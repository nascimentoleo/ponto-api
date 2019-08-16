package com.example.ponto.api.controllers;

import com.example.ponto.api.dtos.CadastroPjDto;
import com.example.ponto.api.entities.Empresa;
import com.example.ponto.api.entities.Funcionario;
import com.example.ponto.api.enums.PerfilEnum;
import com.example.ponto.api.response.Response;
import com.example.ponto.api.services.EmpresaService;
import com.example.ponto.api.services.FuncionarioService;
import com.example.ponto.api.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/api/cadastrar-pj")
@CrossOrigin(origins = "*")
public class CadastroPjController {

    private static final Logger log = LoggerFactory.getLogger(CadastroPjController.class);

    @Autowired
    private FuncionarioService funcionarioService;
    @Autowired
    private EmpresaService empresaService;


    @PostMapping
    public ResponseEntity<Response<CadastroPjDto>> cadastrar(@Valid @RequestBody final CadastroPjDto cadastroPjDto, final BindingResult result) {
        log.info("Cadastrando PJ: {}", cadastroPjDto.toString());
        final Response<CadastroPjDto> response = new Response<>();

        validarDadosExistentes(cadastroPjDto, result);
        final Empresa empresa = converterDtoParaEmpresa(cadastroPjDto);
        final Funcionario funcionario = converterDtoParaFuncionario(cadastroPjDto);

        if (result.hasErrors()) {
            log.error("Erro validando dados de cadastro PJ: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        empresaService.persistir(empresa);
        funcionario.setEmpresa(empresa);
        funcionarioService.persistir(funcionario);

        response.setData(converterCadastroPjDto(funcionario));
        log.info("Usuário PJ cadastrado com sucess! - {} {}", funcionario.getNome(), funcionario.getCpf());
        return ResponseEntity.ok(response);
    }

    private CadastroPjDto converterCadastroPjDto(final Funcionario funcionario) {
        final CadastroPjDto cadastroPjDto = new CadastroPjDto();
        cadastroPjDto.setId(funcionario.getId());
        cadastroPjDto.setNome(funcionario.getNome());
        cadastroPjDto.setEmail(funcionario.getEmail());
        cadastroPjDto.setCnpj(funcionario.getCpf());
        cadastroPjDto.setRazaoSocial(funcionario.getEmpresa().getRazaoSocial());
        cadastroPjDto.setCnpj(funcionario.getEmpresa().getCnpj());
        return cadastroPjDto;
    }

    private Funcionario converterDtoParaFuncionario(final CadastroPjDto cadastroPjDto) {
        final Funcionario funcionario = new Funcionario();
        funcionario.setNome(cadastroPjDto.getNome());
        funcionario.setEmail(cadastroPjDto.getEmail());
        funcionario.setCpf(cadastroPjDto.getCpf());
        funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
        funcionario.setSenha(PasswordUtils.gerarBCrypt(cadastroPjDto.getSenha()));
        return funcionario;
    }

    private Empresa converterDtoParaEmpresa(final CadastroPjDto cadastroPjDto) {
        final Empresa empresa = new Empresa();
        empresa.setCnpj(cadastroPjDto.getCnpj());
        empresa.setRazaoSocial(cadastroPjDto.getRazaoSocial());
        return empresa;
    }

    private void validarDadosExistentes(final CadastroPjDto cadastroPjDto, final BindingResult result) {
        empresaService.buscarPorCnpj(cadastroPjDto.getCnpj()).ifPresent(emp -> result.addError(new ObjectError("empresa", "Empresa já cadastrada")));
        funcionarioService.buscarPorCpf(cadastroPjDto.getCpf()).ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já cadastrado")));
        funcionarioService.buscarPorEmail(cadastroPjDto.getEmail()).ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já cadastrado")));
    }
}
