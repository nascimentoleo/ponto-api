package com.example.ponto.api.services;

import com.example.ponto.api.entities.Funcionario;
import com.example.ponto.api.repositories.FuncionarioRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FuncionarioServiceTest {

    @MockBean
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private FuncionarioService funcionarioService;


    @Test
    public void testPersistirFuncionario() {
        given(funcionarioRepository.save(any(Funcionario.class))).willReturn(new Funcionario());

        Funcionario funcionario = funcionarioService.persistir(new Funcionario());

        assertNotNull(funcionario);
    }

    @Test
    public void testBuscarFuncionarioPorId() {
        given(funcionarioRepository.findById(anyLong())).willReturn(Optional.of(new Funcionario()));

        Optional<Funcionario> funcionario = funcionarioService.buscarPorId(new Random().nextLong());

        assertTrue(funcionario.isPresent());

    }

    @Test
    public void testBuscarFuncionarioPorEmail() {
        given(funcionarioRepository.findByEmail(anyString())).willReturn(new Funcionario());

        Optional<Funcionario> funcionario = funcionarioService.buscarPorEmail("");

        assertTrue(funcionario.isPresent());

    }

    @Test
    public void testBuscarFuncionarioPorCpf() {
        given(funcionarioRepository.findByCpf(anyString())).willReturn(new Funcionario());

        Optional<Funcionario> funcionario = funcionarioService.buscarPorCpf("");

        assertTrue(funcionario.isPresent());

    }
}
