package com.fatec.cliente_backv2.persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager; 
import org.springframework.dao.DataIntegrityViolationException; 

import com.fatec.cliente_backv2.model.Cliente;
import com.fatec.cliente_backv2.service.ClienteRepository;

@DataJpaTest
class Req12AtualizarInformacoesDeClienteTests {
	
	private Cliente cliente;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
    private TestEntityManager entityManager; 

	public void setup() {
		cliente = new Cliente();
		cliente.setCpf("80983098000"); // CPF Válido
		cliente.setNome("Jose da Silva");
		cliente.setCep("01310-100");
		cliente.setEndereco("Av. Paulista");
		cliente.setComplemento("123");
		cliente.setEmail("jose@gmail.com");
		cliente.setDataCadastro();
		clienteRepository.save(cliente);
	}
	
	@Test
	void ct01_quando_cliente_modificado_retorna_informacoes_atualizadas() {
		//Dado
		setup();
		
		//Quando
		cliente.setNome("Carlos Xavier");
		clienteRepository.save(cliente);
		
		//Entao
		Optional<Cliente> clienteAtualizado = clienteRepository.findByCpf("80983098000");
		assertEquals ("Carlos Xavier", clienteAtualizado.get().getNome());
	}

	@Test
	void ct02_quando_atualiza_multiplos_campos_retorna_atualizado() {
		//Dado
		setup();

		//Quando
		cliente.setNome("Jose Atualizado");
		cliente.setEmail("jose.novo@gmail.com");
		cliente.setComplemento("Bloco A");
		clienteRepository.save(cliente);

		//Entao
		Optional<Cliente> clienteOptional = clienteRepository.findByCpf("80983098000");
		Cliente clienteAtualizado = clienteOptional.get();
		
		assertEquals("Jose Atualizado", clienteAtualizado.getNome());
		assertEquals("jose.novo@gmail.com", clienteAtualizado.getEmail());
		assertEquals("Bloco A", clienteAtualizado.getComplemento());
	}

	@Test
    void ct03_quando_atualiza_cpf_para_um_ja_existente_lanca_excecao() {
        Cliente cliente1 = new Cliente();
		cliente1.setCpf("80983098000"); 
		cliente1.setNome("Cliente Um");
		cliente1.setCep("01310-100");
		cliente1.setEndereco("Av. Paulista");
		cliente1.setComplemento("1");
		cliente1.setEmail("cliente1@gmail.com");
		cliente1.setDataCadastro();
        entityManager.persistAndFlush(cliente1); 

        Cliente cliente2 = new Cliente();
		cliente2.setCpf("99504993052"); 
		cliente2.setNome("Cliente Dois");
		cliente2.setCep("01310-100");
		cliente2.setEndereco("Av. Paulista");
		cliente2.setComplemento("2");
		cliente2.setEmail("cliente2@gmail.com");
		cliente2.setDataCadastro();
        entityManager.persistAndFlush(cliente2); 

        // Quando eu tento atualizar o CPF do cliente2 para o CPF do cliente1
       	cliente2.setCpf("80983098000");

        // Entao o sistema deve lançar a exceção de violação de integridade do BANCO
        assertThrows(DataIntegrityViolationException.class, () -> {
            // A exceção ocorrerá aqui 
            clienteRepository.saveAndFlush(cliente2); 
        });
    }
}