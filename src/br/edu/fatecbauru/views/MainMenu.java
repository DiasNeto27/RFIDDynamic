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
	 * Inicializando contéudo do menu em um {@code TreeMap} para garantir a
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
//		if (Cadastro.getInstance().getQtdCampos() > 0) {
//			while (true) { //controla a validação da opção
//				System.out.println("Já há campos definidos, deseja redefinir o cadastro? " + Resources.getOpcoesFormatadas());
//				String opcao = userInput.next(); userInput.nextLine();
//				if (opcao.toUpperCase().charAt(0) == Resources.OPCAO_SIM) {
//					Cadastro.getInstance().redefinir();
//					System.out.println("Cadastro redefinido.");
//					break;
//				} else if (opcao.toUpperCase().charAt(0) == Resources.OPCAO_NAO) {
//					return;
//				}
//			}
//		}
		
	    int campos = 2;
		while (true) { //controla o numero de campos que serão cadastros pelo usuário
			Resources.clearScreen();
			System.out.println("Digite o nome do campo: ");
			String nomeCampo = userInput.nextLine();
			if (nomeCampo.length() <= 0) {
				System.out.println("Entrada vazia digite novamente.");
				continue;
			}

			Cadastro.getInstance().addCampo(nomeCampo);
			campos--;
			
			if (campos == 0){
				break;
			}

		}
		
		System.out.println("Cadastro definido com êxito");
	}
	
	
	public void cadastrar(){
		Iterator<Campo> iterator = Cadastro.getInstance().getCampos().iterator();

		while (iterator.hasNext()) {
			Campo campo = iterator.next();
			System.out.println("Digite o contéudo do campo " + campo.getNome() + ":");
			String valor = userInput.next(); userInput.nextLine();
			Cadastro.getInstance().addCampo(campo.getNome(), valor);
		}
		
		
		//gravar cadastro no RFID
		Cadastro.getInstance().persistirDadosRFID(rfid);
			
	}
	
	public void lerDados(){
		try {
			System.out.println(rfid.conectar());
		} catch (Exception e) {		
			e.printStackTrace();
		}
		rfid.redefineVariaveis();
		System.out.println("Request " + rfid.request());
	    System.out.println("Anticoll: " + rfid.antiCollision());
	    System.out.println("Select: " + rfid.selecionar());
	   Cadastro.getInstance().lerDadosRFID(rfid);
	   System.out.println("Finalizado");
	   System.exit(0);
	}
	
	/** Imprime na tela os nomes dos colaboradores deste projeto */
	public void sobre() {
		System.out.println("\n");
		System.out.println("Autores: " + InfoUtils.getAuthors());
		System.out.println("Cordenadores: " + InfoUtils.getCordinators());
	}

	public MainMenu() {
		int value = 0;
//
//		do {
			System.out.println(".:::. " + InfoUtils.getProjectName() + " versão: " + InfoUtils.getVersion() + ".:::.");
			System.out.println("Nosso teste de integração deverá gravar dois campos dinâmicos no RFID que escolhermos e mostra-lo na tela. Seguiremos os passos: ");
			System.out.println("Passo 1: Vamos testar a conexão com o cartão");
			conectar();
			System.out.println("Passo 2: Agora que esta conectando, vamos definir dois campos para gravar no RFID");
			definirCadastro();	
			System.out.println("Passo 3: Agora que o cadastro foi definido vamos grava-lo no RFID");
			cadastrar();
			System.out.println("Passo 4: Agora vamos ler");
			lerDados();
			
//			printMenu();
//			System.out.println("Digite a opção: ");
//			value = userInput.nextInt();
//			userInput.nextLine();
//			switch (value) {
//			case MENU_CONECTAR:
//				conectar();
//				break;
//			case MENU_DEFINIR_CADASTRO:
//			    definirCadastro();
//				break;
//			case MENU_CADASTRAR:
//				cadastrar();
//				break;
//			case MENU_LER_DADOS:
//				lerDados();
//				break;
//			case MENU_SOBRE:
//				break;
//			case MENU_SAIR:
//				break;
//			default:
//				break;
//			}

//		} while (value != MENU_SAIR);

	}
}
