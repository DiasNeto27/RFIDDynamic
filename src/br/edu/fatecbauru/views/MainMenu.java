package br.edu.fatecbauru.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;



import br.edu.fatecbauru.controllers.Cadastro;
import br.edu.fatecbauru.controllers.Campo;
import br.edu.fatecbauru.controllers.RFID;
import br.edu.fatecbauru.controllers.util.InfoUtils;
import br.edu.fatecbauru.controllers.util.Resources;

public class MainMenu {
	/** inicialia a classe Scanner para receber entradas do usuário */
	Scanner userInput = new Scanner(System.in);

	/** Definindo números correspodentes as opções de menu */
	final int MENU_CONECTAR = 1;
	final int MENU_DEFINIR_CADASTRO = 2;
	final int MENU_CADASTRAR = 3;
	final int MENU_LER_DADOS = 4;
	final int MENU_SOBRE = 5;
	final int MENU_SAIR = 6;

	/**
	 * Inicializando conteúdo do menu em um {@code TreeMap} para garantir a
	 * ordem correta (crescente) das opções
	 */
	Map<Integer, String> menu = new TreeMap<Integer, String>() {
		{
			put(MENU_CONECTAR, "Conectar-se ao RFID");
			put(MENU_DEFINIR_CADASTRO, "Definir cadastro");
			put(MENU_CADASTRAR, "Cadastrar cadastro definido");
			put(MENU_LER_DADOS, "Ler cadastro definido");
			put(MENU_SOBRE, "Sobre");
			put(MENU_SAIR, "Sair");
		}
	};

	/**
	 * Mostra o menu na tela de forma já formatada em ordem crescente das opções
	 */
	public void printMenu() {
		Iterator<Map.Entry<Integer, String>> iterator = menu.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<Integer, String> entry = iterator.next();
			System.out.println("(" + entry.getKey() + ") : " + entry.getValue());
		}
	}

	RFID rfid = new RFID();

	/** conecta ao RFID */
	public boolean conectar() {
		try {
			int porta = rfid.conectar();
			System.out.println("Conectado na porta : " + porta);
			return true;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
	}

	
	/**
	 * Define o cadastro a ser enviado para o RFID
	 */
	public void definirCadastro() {
			Cadastro.getInstance().redefinir();
		
	    int campos = 0;
	    
		while (true) { //controla o numero de campos que ser�o cadastros pelo usu�rio
			Resources.clearScreen();
			System.out.println("Digite o nome do campo ou 0 para próximo passo ");
			String nomeCampo = userInput.nextLine();
			if (nomeCampo.length() <= 0) {
				System.out.println("Entrada vazia digite novamente.");
				continue;
			}
			
			if (nomeCampo.equalsIgnoreCase("0")){
				System.out.println("Processo finalizado pelo usuário");
				break;				
			}

			Cadastro.getInstance().addCampo(nomeCampo);
			campos++;
			
			if (campos == RFID.MAX_CAMPOS){
				System.out.println("O número máximo de campos foi atingido");
				break;
			}

		}
		
		System.out.println("Cadastro definido com êxito");
	}
	
	
	public void cadastrar(){
		Iterator<Campo> iterator = Cadastro.getInstance().getCampos().iterator();

		while (iterator.hasNext()) {
			Campo campo = iterator.next();
			System.out.println("Digite o conteúdo do campo " + campo.getNome() + ":");
			String valor = userInput.next(); userInput.nextLine();
			Cadastro.getInstance().addCampo(campo.getNome(), valor);
		}
		
		
		//gravar cadastro no RFID
		Cadastro.getInstance().persistirDadosRFID(rfid);
			
	}
	
	public boolean carregarCartao(Double valor){
		//por padrão vamos usar o bloco 4
		rfid.verificaCartao(true);
		rfid.autenticar(4);
		return rfid.gravarInformacao(valor.toString(), 4) == 0;
		
	}
	
	public boolean cobraCartao(Double valor){
		rfid.autenticar(4);
		Double credito = Double.parseDouble(rfid.lerInformacao(4));
	    if (credito >= valor){
	    	rfid.beep(30);
	    	Double creditoAnterior = credito;
	    	credito -= valor;
	    	rfid.gravarInformacao(credito.toString(), 4);
	    	System.out.println("Crédito anterior: R$" +  Double.toString(creditoAnterior));
	    	System.out.println("Crédito atual: R$" + Double.toString(credito));
	    	System.out.println("Custo passagem: R$" + Double.toString(valor));
	    	return true;
	    }else{
	    	rfid.beep(15);
	    	rfid.beep(15);
	    	System.err.println("Saldo insuficiente");
	    	return false;
	    }
		
	}
	
	public void lerDados(){
	
		rfid.redefineVariaveis();
		rfid.verificaCartao(true);
	   Cadastro.getInstance().lerDadosRFID(rfid);
	   rfid.beep(30);
	   
	}
	
	/** Imprime na tela os nomes dos colaboradores deste projeto */
	public void sobre() {
		System.out.println("\n");
		System.out.println("Autores: " + InfoUtils.getAuthors());
		System.out.println("Cordenadores: " + InfoUtils.getCordinators());
	}

	
	public void aguardar(Integer segundos){
		try {
			Thread.sleep(segundos * 1000);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}
	}
	public MainMenu() {
			System.out.println(".:::. " + InfoUtils.getProjectName() + " versão: " + InfoUtils.getVersion() + ".:::.");
			System.out.println("Nosso teste de integração deverá gravar campos dinâmicos no RFID que escolhermos e mostra-lo na tela. Seguiremos os passos: ");
			System.out.println("Passo 1: Vamos testar a conexão com o equipamento");
			if (!conectar()){
				System.err.println("Falha ao tentar conectar com o equipamento");
				return;
			}
			System.out.println("Passo 2: Agora vamos definir os campos que desejamos salvar no cartão");
			definirCadastro();
			System.out.println("Passo 3: Preencha os dados conforme é pedido");
			cadastrar();
			System.out.println("Passo 4: Agora que salvamos no cartão, vamos ler o cont�udo e mostra-lo");
			lerDados();
			System.out.println("---------------------------------");
			System.out.println("Teste de integração finalizado");
			
			
			System.out.println("Podemos agora simular uma catraca eletrônica de um ônibus");
			System.out.println("Vou carregar R$ 10,00 no seu cartão");
			if (!carregarCartao(10.00)){
				System.err.println("Não foi possivel carregar cartão");
				return;
			}else{
				System.out.println("Seu cartão foi carregado com R$ 10,00.");
				System.out.println("Remove o cartão do leitor");
				aguardar(7);
			}
			
			System.out.println("A passagem custa R$ 2,60");
			
			System.out.println("---------- CATRACA ELETRÔNICA ATIVA ----------");
			while(true){
				if (rfid.verificaCartao(false)){
					if (!cobraCartao(2.60)){
						break;
					}
					aguardar(5);
				}				
			}
			
			
			sobre();


				
			System.exit(0);

	}
}
