package com.example.ponto.api.repositories;

import com.example.ponto.api.entities.Empresa;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EmpresaRepositoryTest {

    @Autowired
    private EmpresaRepository empresaRepository;

    private static final String CNPJ = "51463645000100";

    @Before
    public void setUp() {
        Empresa empresa = new Empresa();
        empresa.setRazaoSocial("Empresa Test");
        empresa.setCnpj(CNPJ);
        empresaRepository.save(empresa);
    }

    @After
    public final void tearDown() {
        empresaRepository.deleteAll();
    }

    @Test
    public void testBuscarPorCnpj() {
        Empresa empresa = empresaRepository.findByCnpj(CNPJ);

        assertEquals(empresa.getCnpj(), CNPJ);
    }
}
