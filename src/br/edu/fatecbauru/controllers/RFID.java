package br.edu.fatecbauru.controllers;

import java.util.Scanner;

import br.edu.fatecbauru.controllers.bridge.MasterRDImpl;
import br.edu.fatecbauru.controllers.util.InfoUtils;
import br.edu.fatecbauru.controllers.util.Resources;

import com.sun.jna.Native;

public class RFID {

	private MasterRDImpl dll = null;
	private short icdev = 0x0000;
	private int[] pTagType = new int[]{0};
	private char[] pSnr = new char[200];
	private byte[] plen = new byte[]{0};
	byte[] pSize=new byte[]{0};
	
	//bloco atual
	int current_block = 4;
	
	public RFID(){
		String path = System.getProperty("user.dir");		
		System.out.println(path);
		String javaLibraryPath =  System.getProperty("java.library.path");
		System.setProperty("java.library.path", javaLibraryPath + ";" + path);		
		
		dll = (MasterRDImpl) Native.loadLibrary("MasterRD", MasterRDImpl.class);
	}
	
	public int nextBlock(){
		return current_block++;
	}
	

	
	/**
	 * Conecta-se ao RFID pela por disponível 
	 * os <i>bauds</i> existentes são:
	 * <i>9600</i>
	 *  <i>19200</i>
	 *  <i>57600</i>
	 *  <i>115200</i> - indicado conexões USB (default)
	 * @return retorna a porta no qual foi conectado, caso contrário retornará -1
	 */
	public int conectar() throws Exception {		 
		int baud = 115200; //default
		int conStatus = -1;
		int port = 1;
		for (; port < 10; port++){ //testa as portas de 1 a 9
			  
			  conStatus = dll.rf_init_com(port, baud);
			  if (conStatus == 0){			 
				  return port;
			  }
		}
		
		if (conStatus != 0){
			throw new Exception(InfoUtils.getErrorMessage(conStatus));
		}else{
			return port;
		}		    			
	}
	
	public int request(){
		
		byte model = 0x52;		
		return dll.rf_request(icdev, model, pTagType);
	}
	
	public int antiCollision(){
		int result = dll.rf_anticoll(icdev, (char) 4, pSnr, plen);
		//serial
		String serial= "";
		for (int i =0; i < 2 ; i++){
			serial += Integer.toHexString((int) pSnr[i]);
		}
		
		return result;
	}
	
	/**
	 * O cartão vai ficar no estado de ativo depois de receber este comando
	 * @return retorna o resultado 0 se tiver êxito caso contrário retornará diferente de 0
	 */
	public int selecionar(){
		return dll.rf_select(icdev, pSnr, plen[0], pSize);
	}
	
	public int autenticar(int bloco){
	
		byte model = 0x60; //Key A
		byte pKeyn = (byte)0xFF;
		byte[] pKey = new byte[]{pKeyn,pKeyn,pKeyn,pKeyn,pKeyn,pKeyn,pKeyn};
	    return dll.rf_M1_authentication2(icdev, (char) model, (byte) bloco, pKey);
	}
	
	public String lerInformacao(int bloco){
		request();
		antiCollision();
		selecionar();
//		System.out.println("Request " + request());
//	    System.out.println("Anticoll: " + antiCollision());
//	    System.out.println("Select: " + selecionar());
	
		
		//antes devo autenticar
		autenticar(bloco);
		
		
		byte[] pData = new byte[200];
		byte[] pDataLen = new byte[]{0};
		
		dll.rf_M1_read(icdev, (byte) bloco, pData, pDataLen);
		
		//converte conteúdo
		String conteudo = "";
		String hexConteudo = "";
		
		for (int i =0; i < pDataLen[0]; i++){			
			hexConteudo +=  (char) pData[i];		
		}
		
		byte[] bytesConteudo = Resources.fromHexString(hexConteudo, 0, 16);
		
		for (int i =0; i < bytesConteudo.length; i++){
			conteudo += (char)  bytesConteudo[i];
		}
		
		return conteudo;
	}
	
	public int gravarInformacao(String valor, int bloco){
		System.out.println("Request " + request());
	    System.out.println("Anticoll: " + antiCollision());
	    System.out.println("Select: " + selecionar());

//		request();
//		antiCollision();
//		selecionar();
		
		if (valor.length() > 16){
			System.out.println("Texto excedeu 16 bytes: " + valor);
		}
		
		String hexStr = "";
		
		for (int i=0; i < valor.length(); i++){
			hexStr += Integer.toHexString((int) valor.charAt(i));
		}
		
		for (int i=0; i <= (16 - valor.length()); i++){
			hexStr += "00";
		}
		
		//antes devo autenticar
		autenticar(bloco);
		
		//escrevendo a string no cartão
		return dll.rf_M1_write(icdev, (byte) bloco, hexStr.getBytes());
	}
}
