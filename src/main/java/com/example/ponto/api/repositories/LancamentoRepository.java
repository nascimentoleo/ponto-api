package com.example.ponto.api.repositories;

import com.example.ponto.api.entities.Lancamento;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.List;

@Transactional(readOnly = true)
@NamedQueries( {
        @NamedQuery(name = "LancamentoRepository, findByFuncionarioId",
                query="SELECT lanc FROM Lancamento lanc WHERE lanc.funcionario.id =:funcionarioId")
})
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    List<Lancamento> findByFuncionarioId(@Param("funcionarioId") Long funcionarioId);

    Page<Lancamento> findByFuncionarioId(@Param("funcionarioId") Long funcionarioId, Pageable pageable);
}
