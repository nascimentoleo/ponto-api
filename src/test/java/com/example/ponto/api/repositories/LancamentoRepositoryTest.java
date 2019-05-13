package com.example.ponto.api.repositories;

import com.example.ponto.api.entities.Empresa;
import com.example.ponto.api.entities.Funcionario;
import com.example.ponto.api.entities.Lancamento;
import com.example.ponto.api.enums.PerfilEnum;
import com.example.ponto.api.enums.TipoEnum;
import com.example.ponto.api.utils.PasswordUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

    @Autowired
    private FuncionarioRepository funcionarioRepository;
    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private LancamentoRepository lancamentoRepository;

    private Long funcionarioId;

    @Before
    public void setUp() {
        Empresa empresa = empresaRepository.save(obterDadosEmpresa());
        Funcionario funcionario = funcionarioRepository.save(obterDadosFuncionario(empresa));

        this.funcionarioId = funcionario.getId();

        lancamentoRepository.save(obterDadosLancamentos(funcionario));
        lancamentoRepository.save(obterDadosLancamentos(funcionario));
    }

    @After
    public void tearDown() {
        empresaRepository.deleteAll();
    }

    @Test
    public void testBuscarLancamentosPorFuncionarioId() {
        List<Lancamento> lancamentos = lancamentoRepository.findByFuncionarioId(funcionarioId);

        assertEquals(2, lancamentos.size());
    }

    @Test
    public void testBuscarLancamentosPorFuncionarioIdPaginado() {
        PageRequest page = new PageRequest(0, 10);

        Page<Lancamento> lancamentos = lancamentoRepository.findByFuncionarioId(funcionarioId, page);

        assertEquals(2, lancamentos.getTotalElements());

    }

    private Lancamento obterDadosLancamentos(Funcionario funcionario) {
        Lancamento lancamento = new Lancamento();
        lancamento.setData(new Date());
        lancamento.setTipo(TipoEnum.INICIO_ALMOCO);
        lancamento.setFuncionario(funcionario);
        return lancamento;
    }

    private Funcionario obterDadosFuncionario(Empresa empresa) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Funcionario Teste");
        funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
        funcionario.setSenha(PasswordUtils.gerarBCrypt("123456"));
        funcionario.setCpf("11111111111");
        funcionario.setEmail("email@email.com");
        funcionario.setEmpresa(empresa);
        return funcionario;
    }

    private Empresa obterDadosEmpresa() {
        Empresa empresa = new Empresa();
        empresa.setRazaoSocial("Empresa teste");
        empresa.setCnpj("51463645000100");
        return empresa;
    }
}
