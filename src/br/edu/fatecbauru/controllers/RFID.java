package br.edu.fatecbauru.controllers;

import java.util.Arrays;
import java.util.Scanner;




import br.edu.fatecbauru.controllers.bridge.MasterRDImpl;
import br.edu.fatecbauru.controllers.util.InfoUtils;





import com.sun.jna.Native;

public class RFID {	
	public static final int MAX_CAMPOS = 47;
	private MasterRDImpl dll = null;
	private short icdev = 0x0000; //corresponde ao device ID, por default deve ser 0
	private int[] pTagType = new int[]{0};
	private char[] pSnr = new char[200];
	private byte[] plen = new byte[]{0};
	byte[] pSize=new byte[]{0};
	String hexStr = "";
	byte[] pData = new byte[100];
	//bloco atual
	int blocoAtual= 1;
	
	/**
	 *  Este construtor faz a inclusão do caminho da DLL no library path do Java e a carrega em memória
	 */
	public RFID(){
		String path = System.getProperty("user.dir");		
		System.out.println(path);
		String javaLibraryPath =  System.getProperty("java.library.path");
		System.setProperty("java.library.path", javaLibraryPath + ";" + path);		
		
		dll = (MasterRDImpl) Native.loadLibrary("MasterRD", MasterRDImpl.class);
	}
	
	/**
	 * Este método retorna o próximo bloco disponível
	 * @return Retorna o número referente ao bloco disponivel
	 */
	public int getBlocoDisponivel(){
		while(isBlocoChave(++blocoAtual));		
		return blocoAtual;
	}
	

	/**
	 * Este método verifica se o {@code bloco} não é um bloco onde armazena-se a chave do setor
	 * a fórmula para saber qual é o bloco chave do setor em cartões s50 se dá pela equação x=s*4+3
	 * onde <i>s</i> é o setor e <i>x</i> o bloco encontrado.
	 * Para sabermos de qual setor é o {@code bloco} é só dividirmos o número do bloco por 4
	 * já que cada setor tem 4 blocos. 
	 * @param bloco O número que identifica o bloco
	 * @return {@code true} se {@code bloco} for chave do setor, caso contrário, retorna {@code false}
	 */
	public boolean isBlocoChave(int bloco){
		int setor = bloco / 4;		 
		return (setor * 4 + 3) == bloco; 
	}

	
	/**
	 * Este método tenta fazer a conexão com o equipamento RFID testando as portas de 1 a 9 
	 * os <i>bauds</i> existentes são:
	 * <i>9600</i>
	 *  <i>19200</i>
	 *  <i>57600</i>
	 *  <i>115200</i> - indicado conexões USB (default)
	 * @return retorna a porta no qual foi conectado o equipamento, caso contrário retornará -1
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
	
	/**
	 * Este método fará uma requisição inicial ao cartão
	 * 
	 * @return
	 */
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
		int retorno = dll.rf_select(icdev, pSnr, (byte) 4, plen);		
		return retorno;
	}
	
	public void redefineVariaveis(){
		pTagType = new int[]{0};
		pSnr = new char[200];
		plen = new byte[]{0};
		pSize=new byte[]{0};
		blocoAtual = 1;
		pData = new byte[100];
		
	}
	
	
	public boolean verificaCartao(){
		System.out.println("Coloque o cartão proximo do leitor e pressione [ENTER] para continuar");
		Scanner s = new Scanner(System.in);		
		s.nextLine();
		request();		
		antiCollision();
		int result  = selecionar();
		if (result == 0){
			System.out.println("Cartão detectado com êxito");
			return true;
		}else{
			System.out.println("Não conseguiu identificar o cartão");
			return false;
		}
	
	}
	
	public int autenticar(int bloco){
	
		byte model = 0x60; //Key A
		byte[] pKey = new byte[]{(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255};			
	    return dll.rf_M1_authentication2(icdev, (char) model, (byte) bloco, pKey);
	}
	
	public String lerInformacao(int bloco){

		
	
		
		//antes devo autenticar
		System.out.println("Auth: " +  autenticar(bloco));
		
		
		byte[] pData = new byte[100];
		byte[] pDataLen = new byte[]{0};
	
		dll.rf_M1_read(icdev, (byte) bloco, pData, pDataLen);
		
		//converte conteúdo
		
		String hexConteudo = "";
		
		for (int i =0; i < pDataLen[0]; i++){			
			hexConteudo +=  (char) pData[i];		
		}
	
		
		return hexConteudo;
	}
	
	public int gravarInformacao(String valor, int bloco){
		

		
		if (valor.length() > 16){
			System.out.println("Texto excedeu 16 bytes: " + valor);
		}
		

		
		for (int i = 0; i < 16; i++) {
			if (i < valor.length()) {
				pData[i] = ((byte) valor.charAt(i));
			} else {
				pData[i] = ((byte) 0);
			}

		}
		
	
		//antes devo autenticar
		System.out.println("Auth: " + autenticar(bloco)); 
		
		//escrevendo a string no cartão
		return dll.rf_M1_write(icdev, (byte) bloco, pData);
	
	}
}
