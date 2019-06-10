package com.example.ponto.api.services;

import com.example.ponto.api.entities.Empresa;
import com.example.ponto.api.repositories.EmpresaRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EmpresaServiceTest {

    @MockBean
    private EmpresaRepository empresaRepository;
    @Autowired
    private EmpresaService empresaService;
    private static final String CNPJ = "51463645000100";

    @Test
    public void testBuscarEmpresaPorCnpj() {
        given(empresaRepository.findByCnpj(anyString())).willReturn(new Empresa());

        Optional<Empresa> empresa = empresaService.buscarPorCnpj(CNPJ);

        assertTrue(empresa.isPresent());
    }

    @Test
    public void testPersistirEmpresa() {
        given(empresaRepository.save(any(Empresa.class))).willReturn(new Empresa());
        Empresa empresa = empresaService.persistir(new Empresa());

        assertNotNull(empresa);
    }


}
