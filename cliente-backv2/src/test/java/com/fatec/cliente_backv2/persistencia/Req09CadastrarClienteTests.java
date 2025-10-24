package com.fatec.cliente_backv2.persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import com.fatec.cliente_backv2.model.Cliente;
import com.fatec.cliente_backv2.service.ClienteRepository;
@DataJpaTest
class Req09CadastrarClienteTests {

	private Cliente cliente;
	@Autowired
	private ClienteRepository clienteRepository;
	// Injete o TestEntityManager
	@Autowired
	private TestEntityManager entityManager;
	
	public void setup() {
		cliente = new Cliente();
		cliente.setCpf("80983098000");
		cliente.setNome("Jose da Silva");
		cliente.setCep("01310-100");
		cliente.setEndereco("Av. Paulista");
		cliente.setComplemento("123");
		cliente.setEmail("jose@gmail.com");
		cliente.setDataCadastro();
	}
	public String dataAtual() {
		LocalDate dataAtual = LocalDate.now();
		DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return dataAtual.format(pattern);
	}
		
	@Test
	void ct01_quando_dados_validos_cliente_cadastrado_com_sucesso() {
		// Dado - que o vendedor forneceu as informações do novo cliente cpf, nome, cep, complemento e e-mail validos
		setup();
		// Quando - o vendedor confirma o cadastro 
		Cliente novoCliente = clienteRepository.save(cliente);
		// Entao - o novo cliente é cadastrado com sucesso no sistema 
		// E a data de cadastro é registrada
		// E o endereço é preenchido automaticamente com base no CEP fornecido.
		assertEquals(1, clienteRepository.count());
		assertEquals(dataAtual(), novoCliente.getDataCadastro());
		assertEquals("Av. Paulista", novoCliente.getEndereco());
	}
	@Test
	void ct02_quando_dados_invalidos_retorna_invalido() {
		try {
			// Dado - que o vendedor deixou o CPF vazio na tela de cadastro de cliente
			setup();
			cliente.setCpf("");
			// Quando - confirmo a operacao de cadastro
			fail("deveria falhar cpf invalido");
		} catch (Exception e) {
			// Entao - o sistema exibe uma mensagem de CPF inválido E o cliente não é cadastrado
			assertEquals("CPF invalido", e.getMessage());
		}
	}
	@Test
	void ct03_quando_cpf_ja_cadastrado_deve_lancar_excecao() {
	    // 1. Dado - o cpf do cliente já está cadastrado.
	    setup();
	    entityManager.persistAndFlush(cliente); //commit
	    // O flush do EntityManager garante a sincronização e ativa a restrição única para a próxima transação lógica.
	    // 2. Quando o vendor confirma a operação de cadastro para um cliente duplicado.
	    Cliente clienteDuplicado = new Cliente();
	    clienteDuplicado.setCpf("80983098000");
	    clienteDuplicado.setNome("Maria da Silva"); // Use um nome diferente para clareza
	    clienteDuplicado.setCep("01310-100");
	    clienteDuplicado.setEndereco("Av. Paulista");
	    clienteDuplicado.setComplemento("123");
	    clienteDuplicado.setEmail("maria@gmail.com");
	    clienteDuplicado.setDataCadastro();
	    // 3. Entao - o sistema exibe uma mensagem de erro informando que o cliente ja esta cadastrado 
	    assertThrows(DataIntegrityViolationException.class, () -> {
	    	clienteRepository.saveAndFlush(clienteDuplicado);
	    });
	}
	@Test
	void ct04_quando_dados_invalidos_retorna_erro() {
		try {
			// Dado - que o vendedor deixou o CPF vazio na tela de cadastro de cliente
			setup();
			cliente.setCpf(" ");
			// Quando - confirmo a operacao de cadastro
			fail("deveria falhar cpf invalido");
		} catch (Exception e) {
			// Entao - o sistema exibe uma mensagem de CPF inválido E o cliente não é cadastrado
			assertEquals("CPF invalido", e.getMessage());
		}
	}
	@Test
	void ct05_quando_dados_invalidos_retorna_erro() {
		try {
			// Dado - que o vendedor deixou o CPF vazio na tela de cadastro de cliente
			setup();
			cliente.setCpf(null);
			// Quando - confirmo a operacao de cadastro
			fail("deveria falhar cpf invalido");
		} catch (Exception e) {
			// Entao - o sistema exibe uma mensagem de CPF inválido E o cliente não é cadastrado
			assertEquals("CPF invalido", e.getMessage());
		}
	}
		@Test
	void ct06_quando_dados_invalidos_retorna_erro() {
		try {
			// Dado - que o vendedor deixou o CPF vazio na tela de cadastro de cliente
			setup();
			cliente.setCpf("aaaaaaaaaaa");
			// Quando - confirmo a operacao de cadastro
			fail("deveria falhar cpf invalido");
		} catch (Exception e) {
			// Entao - o sistema exibe uma mensagem de CPF inválido E o cliente não é cadastrado
			assertEquals("CPF invalido", e.getMessage());
		}
	}

		@Test
	void ct07_quando_dados_invalidos_retorna_erro() {
		try {
			// Dado - que o vendedor deixou o CPF vazio na tela de cadastro de cliente
			setup();
			cliente.setNome("");
			// Quando - confirmo a operacao de cadastro
			fail("deveria falhar nome invalido");
		} catch (Exception e) {
			// Entao - o sistema exibe uma mensagem de nome inválido E o cliente não é cadastrado
			assertEquals("O nome não deve estar em branco", e.getMessage());
		}
	}
		@Test
	void ct08_quando_dados_invalidos_retorna_erro() {
		try {
			// Dado - que o vendedor deixou o CPF vazio na tela de cadastro de cliente
			setup();
			cliente.setNome(null);	
			// Quando - confirmo a operacao de cadastro
			fail("deveria falhar nome invalido");
		} catch (Exception e) {
			// Entao - o sistema exibe uma mensagem de nome inválido E o cliente não é cadastrado
			assertEquals("O nome não deve estar em branco", e.getMessage());
		}
	}
		@Test
	void ct09_quando_dados_invalidos_retorna_erro() {
		try {
			// Dado - que o vendedor deixou o CPF vazio na tela de cadastro de cliente
			setup();
			cliente.setCep("");
			// Quando - confirmo a operacao de cadastro
			fail("CEP invalido");
		} catch (Exception e) {
			// Entao - o sistema exibe uma mensagem de cep inválido E o cliente não é cadastrado
			assertEquals("CEP invalido", e.getMessage());	
		}
	}
}