package com.example.ponto.api.controllers;

import com.example.ponto.api.dtos.CadastroPFDto;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@RestController
@RequestMapping("/api/cadastrar-pf")
@CrossOrigin(origins = "*")
public class CadastroPFController {

    private static final Logger log = LoggerFactory.getLogger(CadastroPFController.class);

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private FuncionarioService funcionarioService;

    public CadastroPFController() {
    }

    @PostMapping
    public ResponseEntity<Response<CadastroPFDto>> cadastrar(@Valid @RequestBody final CadastroPFDto cadastroPFDto, final BindingResult result) throws NoSuchAlgorithmException {
        log.info("Cadastrando PF: {}", cadastroPFDto.toString());
        final Response<CadastroPFDto> response = new Response<>();
        validarDadosExistentes(cadastroPFDto, result);
        final Funcionario funcionario = converterDtoParaFuncionario(cadastroPFDto, result);

        if (result.hasErrors()) {
            log.error("Erro validando dados de cadastro PF: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        final Optional<Empresa> empresa = empresaService.buscarPorCnpj(cadastroPFDto.getCnpj());
        empresa.ifPresent(funcionario::setEmpresa);
        funcionarioService.persistir(funcionario);

        response.setData(converterCadastroPFDto(funcionario));
        return ResponseEntity.ok(response);
    }

    private CadastroPFDto converterCadastroPFDto(final Funcionario funcionario) {
        final CadastroPFDto cadastroPFDto = new CadastroPFDto();
        cadastroPFDto.setId(funcionario.getId());
        cadastroPFDto.setNome(funcionario.getNome());
        cadastroPFDto.setEmail(funcionario.getEmail());
        cadastroPFDto.setCpf(funcionario.getCpf());
        funcionario.getQtdHorasAlmocoOpt()
                .ifPresent(qtdHorasAlmoco -> cadastroPFDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
        funcionario.getQtdHorasTrabalhoDiaOpt()
                .ifPresent(qtdHorasTrabalhoDia -> cadastroPFDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabalhoDia))));
        funcionario.getValorHoraOpt()
                .ifPresent(valorHora -> cadastroPFDto.setValorHora(Optional.of(valorHora.toString())));
        return cadastroPFDto;
    }

    private Funcionario converterDtoParaFuncionario(final CadastroPFDto cadastroPFDto, final BindingResult result) {
        final Funcionario funcionario = new Funcionario();
        funcionario.setNome(cadastroPFDto.getNome());
        funcionario.setEmail(cadastroPFDto.getEmail());
        funcionario.setCpf(cadastroPFDto.getCpf());
        funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
        funcionario.setSenha(PasswordUtils.gerarBCrypt(cadastroPFDto.getSenha()));
        cadastroPFDto.getQtdHorasAlmoco()
                .ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));
        cadastroPFDto.getQtdHorasTrabalhoDia()
                .ifPresent(qtdHorasTrabalhoDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabalhoDia)));
        cadastroPFDto.getValorHora().ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));
        return funcionario;
    }

    private void validarDadosExistentes(final CadastroPFDto cadastroPFDto, final BindingResult result) {
        final Optional<Empresa> empresa = empresaService.buscarPorCnpj(cadastroPFDto.getCnpj());

        if (!empresa.isPresent()) {
            result.addError(new ObjectError("empresa", "Emoresa não cadastrada!"));
        }

        funcionarioService.buscarPorCpf(cadastroPFDto.getCpf())
                .ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existe")));

        funcionarioService.buscarPorEmail(cadastroPFDto.getEmail())
                .ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existe")));
    }
}
