package br.edu.fatecbauru.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.spec.GCMParameterSpec;

import br.edu.fatecbauru.controllers.util.InfoUtils;
import br.edu.fatecbauru.controllers.util.Resources;

/**
 * Esta classe � onde � armazenado o cadastro definido pelo usu�rio, ela segue o
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
	 * Esta fun��o adicionar� um campo no {@code ArrayList<Campo>} <i>campos</i>, 
	 * esta fun��o substitui o valor caso o nome do campo h� exista na lista
	 * 
	 * @param nome O nome do campo
	 * @param valor O valor do campo
	 * @param bloco O bloco no qual o valor do campo ser� gravado
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
			//recupera o bloco no qual a informa��o est� armazenada
			int bloco = campo.getBloco();
			//lendo o que foi escrito
			String dado = rfid.lerInformacao(campo.getBloco());
			System.out.println(campo.getNome() + ": " + dado);
			
		}
	}
	

	public boolean persistirDadosRFID(RFID rfid) {
	    try {
			System.out.println(rfid.conectar());
		} catch (Exception e) {		
			e.printStackTrace();
		}
	    
	    rfid.redefineVariaveis();
	       System.out.println("Request " + rfid.request());
		    System.out.println("Anticoll: " + rfid.antiCollision());
		    System.out.println("Select: " + rfid.selecionar()); 
	    for (Campo campo: this.campos){
	       //gera o bloco que ele ser� gravado
	       int bloco = rfid.nextBlock();
	       campo.setBloco(bloco);
	     
	       int result = rfid.gravarInformacao(campo.getValor().toString(), campo.getBloco());
	   
	       if (result != 0){
	    	   System.err.println("Erro ao tentar gravar dados "+ InfoUtils.getErrorMessage(result));
	       }
	    }
	  
	    System.out.println("Dados gravado com sucesso");
		return true;
	}

}
