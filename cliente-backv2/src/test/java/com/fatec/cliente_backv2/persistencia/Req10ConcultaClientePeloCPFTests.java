package com.fatec.cliente_backv2.persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.fatec.cliente_backv2.model.Cliente;
import com.fatec.cliente_backv2.service.ClienteRepository;

@DataJpaTest
class Req10ConcultaClientePeloCPFTests {

	@Autowired
	private ClienteRepository clienteRepository;
	
	private Cliente cliente;

	public void setup() {
		cliente = new Cliente();
		cliente.setCpf("80983098000");
		cliente.setNome("Jose da Silva");
		cliente.setCep("01310-100");
		cliente.setEndereco("Av. Paulista");
		cliente.setComplemento("123");
		cliente.setEmail("jose@gmail.com");
		cliente.setDataCadastro();
		clienteRepository.save(cliente);
	}
	
	@Test
	void ct01_quando_cliente_cadastrado_retorna_detalhes() {
		
		setup();		
		Optional<Cliente> c = clienteRepository.findByCpf("80983098000");
		assertTrue (c.isPresent());
		assertEquals("Jose da Silva", c.get().getNome());
	}
	
	@Test
	void ct02_quando_cliente_nao_cadastrado_retorna_vazio() {
		Optional<Cliente> c = clienteRepository.findByCpf("80983098001");
		assertTrue (c.isEmpty());
	}

	@Test
	void ct03_quando_cpf_null_retorna_vazio() {
		//Quando - consulto o cpf
		Optional<Cliente> c = clienteRepository.findByCpf(null);
		//Entao - retorna vazio
		assertTrue (c.isEmpty());
	}

	// Os testes ct04, ct05, ct06 validam formatos de CPF
	@Test
	void ct04_quando_cpf_maior_que_11() {
		Optional<Cliente> c = clienteRepository.findByCpf("1111111110560");
		assertTrue (c.isEmpty());
	}
	
	@Test
	void ct05_quando_cpf_menor_que_11_retorna () {
		Optional<Cliente> c = clienteRepository.findByCpf("110560");
		assertTrue (c.isEmpty());
	}
}