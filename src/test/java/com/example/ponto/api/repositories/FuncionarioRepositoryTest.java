package com.example.ponto.api.repositories;

import com.example.ponto.api.entities.Empresa;
import com.example.ponto.api.entities.Funcionario;
import com.example.ponto.api.enums.PerfilEnum;
import com.example.ponto.api.utils.PasswordUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FuncionarioRepositoryTest {

    @Autowired
    private FuncionarioRepository funcionarioRepository;
    @Autowired
    private EmpresaRepository empresaRepository;

    private static final String EMAIL = "email@email.com";
    private static final String CPF = "33322241234";

    @Before
    public void setUp() {
        Empresa empresa = empresaRepository.save(obterDadosEmpresa());
        funcionarioRepository.save(obterDadosFuncionario(empresa));
    }

    @After
    public void tearDown() {
        empresaRepository.deleteAll();
    }

    @Test
    public void testBuscarFuncionarioPorEmail(){
        Funcionario funcionario = funcionarioRepository.findByEmail(EMAIL);

        assertEquals(EMAIL, funcionario.getEmail());
    }

    @Test
    public void testBuscarFuncionarioPorCpf(){
        Funcionario funcionario = funcionarioRepository.findByCpf(CPF);

        assertEquals(CPF, funcionario.getCpf());
    }

    @Test
    public void testBuscarFuncionarioPorEmailECpf(){
        Funcionario funcionario = funcionarioRepository.findByCpfOrEmail(CPF, EMAIL);

        assertNotNull(funcionario);

    }

    @Test
    public void testBuscarFuncionarioPorEmailOuCpfParaEmailInvalido() {
        Funcionario funcionario = funcionarioRepository.findByCpfOrEmail(CPF, "email@invalido.com");

        assertNotNull(funcionario);
    }

    @Test
    public void testBuscarFuncionarioPorEmailOuCpfParaCpfInvalido() {
        Funcionario funcionario = funcionarioRepository.findByCpfOrEmail("11111111111", EMAIL);

        assertNotNull(funcionario);
    }

    private Funcionario obterDadosFuncionario(Empresa empresa) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Funcionario Teste");
        funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
        funcionario.setSenha(PasswordUtils.gerarBCrypt("123456"));
        funcionario.setCpf(CPF);
        funcionario.setEmail(EMAIL);
        funcionario.setEmpresa(empresa);
        return funcionario;
    }

    private Empresa obterDadosEmpresa() {
        Empresa empresa = new Empresa();
        empresa.setRazaoSocial("Empresa teste");
        empresa.setCnpj("`");
        return empresa;
    }
}
