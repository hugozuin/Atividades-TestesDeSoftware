package com.teste.spring.teste;


import com.teste.spring.teste.model.Cliente;
import com.teste.spring.teste.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ClienteRepositoryTest {

    @Autowired
    ClienteRepository repo;

    @Test
    void deveSalvarEConsultarPorEmail() {
        Cliente c = new Cliente();
        c.setNome("Ana");
        c.setEmail("ana@ex.com");
        c.setTelefone("1199999-0000");
        repo.save(c);

        assertThat(repo.existsByEmail("ana@ex.com")).isTrue();
        assertThat(repo.findByEmail("ana@ex.com")).isPresent();
    }

    @Test
    void deveSalvarEConsultarPorId() {
        Cliente c = new Cliente();
        c.setNome("Bruno");
        c.setEmail("bruno@ex.com");
        c.setTelefone("11988888888");
        Cliente salvo = repo.save(c);

        Optional<Cliente> encontrado = repo.findById(salvo.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Bruno");
        assertThat(encontrado.get().getEmail()).isEqualTo("bruno@ex.com");
    }

    @Test
    void deveRetornarFalsoParaEmailQueNaoExiste() {
        assertThat(repo.existsByEmail("inexistente@ex.com")).isFalse();
    }

    @Test
    void deveRetornarOptionalVazioParaEmailInexistente() {
        Optional<Cliente> resultado = repo.findByEmail("naoexiste@ex.com");
        assertThat(resultado).isEmpty();
    }

    @Test
    void deveAtualizarClienteExistente() {
        Cliente c = new Cliente();
        c.setNome("Carlos");
        c.setEmail("carlos@ex.com");
        c.setTelefone("11987654321");
        Cliente salvo = repo.save(c);

        salvo.setNome("Carlos Atualizado");
        salvo.setTelefone("11987654322");
        Cliente atualizado = repo.save(salvo);

        assertThat(atualizado.getId()).isEqualTo(salvo.getId());
        assertThat(atualizado.getNome()).isEqualTo("Carlos Atualizado");
        assertThat(atualizado.getTelefone()).isEqualTo("11987654322");
    }

    @Test
    void deveExcluirClienteExistente() {
        Cliente c = new Cliente();
        c.setNome("Diana");
        c.setEmail("diana@ex.com");
        Cliente salvo = repo.save(c);

        repo.delete(salvo);

        assertThat(repo.findById(salvo.getId())).isEmpty();
        assertThat(repo.existsByEmail("diana@ex.com")).isFalse();
    }

    @Test
    void deveDistinguirEntreMuItiplosClientes() {
        Cliente c1 = new Cliente();
        c1.setNome("Elena");
        c1.setEmail("elena@ex.com");
        Cliente c2 = new Cliente();
        c2.setNome("Felipe");
        c2.setEmail("felipe@ex.com");

        repo.save(c1);
        repo.save(c2);

        assertThat(repo.existsByEmail("elena@ex.com")).isTrue();
        assertThat(repo.existsByEmail("felipe@ex.com")).isTrue();
        assertThat(repo.findByEmail("elena@ex.com")).isPresent();
        assertThat(repo.findByEmail("felipe@ex.com")).isPresent();
    }

    @Test
    void deveSalvarClienteComTodosCamposPreenchidos() {
        Cliente c = new Cliente();
        c.setNome("Gabriela");
        c.setEmail("gabriela@ex.com");
        c.setTelefone("11986543210");
        Cliente salvo = repo.save(c);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Gabriela");
        assertThat(salvo.getEmail()).isEqualTo("gabriela@ex.com");
        assertThat(salvo.getTelefone()).isEqualTo("11986543210");
    }
}
