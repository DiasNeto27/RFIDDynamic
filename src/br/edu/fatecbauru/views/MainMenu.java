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
	/** inicialia a classe Scanner para receber entradas do usu�rio */
	Scanner userInput = new Scanner(System.in);

	/** Definindo n�meros correspodentes as op��es de menu */
	final int MENU_CONECTAR = 1;
	final int MENU_DEFINIR_CADASTRO = 2;
	final int MENU_CADASTRAR = 3;
	final int MENU_LER_DADOS = 4;
	final int MENU_SOBRE = 5;
	final int MENU_SAIR = 6;

	/**
	 * Inicializando cont�udo do menu em um {@code TreeMap} para garantir a
	 * ordem correta (crescente) das op��es
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
	 * Mostra o menu na tela de forma j� formatada em ordem crescente das op��es
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
			System.out.println("Digite o nome do campo ou 0 para pr�ximo passo ");
			String nomeCampo = userInput.nextLine();
			if (nomeCampo.length() <= 0) {
				System.out.println("Entrada vazia digite novamente.");
				continue;
			}
			
			if (nomeCampo.equalsIgnoreCase("0")){
				System.out.println("Processo finalizado pelo usu�rio");
				break;				
			}

			Cadastro.getInstance().addCampo(nomeCampo);
			campos++;
			
			if (campos == RFID.MAX_CAMPOS){
				System.out.println("O n�mero m�ximo de campos foi atingido");
				break;
			}

		}
		
		System.out.println("Cadastro definido com �xito");
	}
	
	
	public void cadastrar(){
		Iterator<Campo> iterator = Cadastro.getInstance().getCampos().iterator();

		while (iterator.hasNext()) {
			Campo campo = iterator.next();
			System.out.println("Digite o cont�udo do campo " + campo.getNome() + ":");
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
		rfid.verificaCartao();
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
			System.out.println(".:::. " + InfoUtils.getProjectName() + " vers�o: " + InfoUtils.getVersion() + ".:::.");
			System.out.println("Nosso teste de integra��o dever� gravar campos din�micos no RFID que escolhermos e mostra-lo na tela. Seguiremos os passos: ");
			System.out.println("Passo 1: Vamos testar a conex�o com o equipamento");
			conectar();
			System.out.println("Passo 2: Agora vamos definir os campos que desejamos salvar no cart�o");
			definirCadastro();	
			System.out.println("Passo 3: Preencha os dados conforme � pedido");
			cadastrar();
			System.out.println("Passo 4: Agora que salvamos no cart�o, vamos ler o cont�udo e mostra-lo");
			lerDados();
			

	}
}
