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
	 *  Este construtor faz a inclus�o do caminho da DLL no library path do Java e a carrega em mem�ria
	 */
	public RFID(){
		String path = System.getProperty("user.dir");		
		System.out.println(path);
		String javaLibraryPath =  System.getProperty("java.library.path");
		System.setProperty("java.library.path", javaLibraryPath + ";" + path);		
		
		dll = (MasterRDImpl) Native.loadLibrary("MasterRD", MasterRDImpl.class);
	}
	
	/**
	 * Este m�todo retorna o pr�ximo bloco dispon�vel
	 * @return Retorna o n�mero referente ao bloco disponivel
	 */
	public int getBlocoDisponivel(){
		while(isBlocoChave(++blocoAtual));		
		return blocoAtual;
	}
	

	/**
	 * Este m�todo verifica se o {@code bloco} n�o � um bloco onde armazena-se a chave do setor
	 * a f�rmula para saber qual � o bloco chave do setor em cart�es s50 se d� pela equa��o x=s*4+3
	 * onde <i>s</i> � o setor e <i>x</i> o bloco encontrado.
	 * Para sabermos de qual setor � o {@code bloco} � s� dividirmos o n�mero do bloco por 4
	 * j� que cada setor tem 4 blocos. 
	 * @param bloco O n�mero que identifica o bloco
	 * @return {@code true} se {@code bloco} for chave do setor, caso contr�rio, retorna {@code false}
	 */
	public boolean isBlocoChave(int bloco){
		int setor = bloco / 4;		 
		return (setor * 4 + 3) == bloco; 
	}

	
	/**
	 * Este m�todo tenta fazer a conex�o com o equipamento RFID testando as portas de 1 a 9 
	 * os <i>bauds</i> existentes s�o:
	 * <i>9600</i>
	 *  <i>19200</i>
	 *  <i>57600</i>
	 *  <i>115200</i> - indicado conex�es USB (default)
	 * @return retorna a porta no qual foi conectado o equipamento, caso contr�rio retornar� -1
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
	 * Este m�todo far� uma requisi��o inicial ao cart�o
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
	 * O cart�o vai ficar no estado de ativo depois de receber este comando
	 * @return retorna o resultado 0 se tiver �xito caso contr�rio retornar� diferente de 0
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
		System.out.println("Coloque o cart�o proximo do leitor e pressione [ENTER] para continuar");
		Scanner s = new Scanner(System.in);		
		s.nextLine();
		request();		
		antiCollision();
		int result  = selecionar();
		if (result == 0){
			System.out.println("Cart�o detectado com �xito");
			return true;
		}else{
			System.out.println("N�o conseguiu identificar o cart�o");
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
		
		//converte conte�do
		
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
		
		//escrevendo a string no cart�o
		return dll.rf_M1_write(icdev, (byte) bloco, pData);
	
	}
}
