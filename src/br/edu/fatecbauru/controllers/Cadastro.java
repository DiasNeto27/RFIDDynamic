package br.edu.fatecbauru.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.spec.GCMParameterSpec;

import br.edu.fatecbauru.controllers.util.InfoUtils;
import br.edu.fatecbauru.views.util.Resources;

/**
 * Esta classe é onde é armazenado o cadastro definido pelo usuário, ela segue o
 * pattern Singleton
 * 
 * @author Dias
 *
 */

public class Cadastro {

	private static Cadastro instancia;
	private ArrayList<Campo> campos = null;

	private Cadastro() {
		campos = new ArrayList<Campo>(){
						
			public boolean add(Campo campo){
				for (int i = 0; i < this.size(); i++){
					Campo campoVerificado = this.get(i);
					if (campoVerificado.getNome().equalsIgnoreCase(campo.getNome())){
						set(i, campo);
						return true;
					}
				}
			 
				return super.add(campo);
			}
			
		
		};
	}

	public static synchronized Cadastro getInstance() {
		if (instancia == null) {
			instancia = new Cadastro();
		}
		return instancia;
	}

	public Campo getCampo(String nome){
		Iterator<Campo> iterator = campos.iterator();
		while (iterator.hasNext()){
			Campo campo = iterator.next();
			if (campo.getNome().equalsIgnoreCase(nome)){
				return campo;
			}
		}
		
		return null;
	}
	
	/**
	 * Esta função adicionará um campo no {@code ArrayList<Campo>} <i>campos</i>, 
	 * esta função substitui o valor caso o nome do campo há exista na lista
	 * 
	 * @param nome O nome do campo
	 * @param valor O valor do campo
	 * @param bloco O bloco no qual o valor do campo será gravado
	 * 
	 */
	public void addCampo(String nome, Object valor, Integer bloco) {
		Campo campo = null;		
		if (isCampoExistente(nome)) {
			campo = getCampo(nome);
		} else {
			campo = new Campo();
			campo.setNome(nome);
		}

		campo.setValor(valor);
		campo.setBloco(bloco);
		
		campos.add(campo);
	}

	public void addCampo(String nome, Object valor) {
		addCampo(nome, valor, null);
	}

	public void addCampo(String nome) {
		addCampo(nome, null, null);
	}

	public ArrayList<Campo> getCampos() {
		return campos;
	}

	public void redefinir() {
		campos.clear();

	}

	public int getQtdCampos() {
		return campos.size();
	}

	public void removerCampo(String nome) {
		campos.remove(nome);
	}

	public boolean isCampoExistente(String nome) {
		return getCampo(nome) != null ? true: false;
	}
	
	public void lerDadosRFID(RFID rfid){
	
		
		System.out.println();
		for (Campo campo: this.campos){
			//recupera o bloco no qual a informação está armazenada
			int bloco = campo.getBloco();
			//lendo o que foi escrito
			String dado = rfid.lerInformacao(campo.getBloco());
			System.out.println(campo.getNome() + ": " + dado);
			
		}
	}
	

	public boolean persistirDadosRFID(RFID rfid) {

	    rfid.redefineVariaveis();
	    rfid.verificaCartao(true);
	    for (Campo campo: this.campos){
	       //gera o bloco que ele será gravado
	       int bloco = rfid.getBlocoDisponivel();
	       campo.setBloco(bloco);
	     
	       int result = rfid.gravarInformacao(campo.getValor().toString(), campo.getBloco());
	   
	       if (result != 0){
	    	   System.err.println("Erro ao tentar gravar dados "+ InfoUtils.getErrorMessage(result));
	       }
	    }
	  
	    rfid.beep(15);
	
	    System.out.println("Dados gravado com sucesso");
		return true;
	}

}
