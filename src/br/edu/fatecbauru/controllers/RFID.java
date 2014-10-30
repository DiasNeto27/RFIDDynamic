package br.edu.fatecbauru.controllers;

import java.util.Arrays;
import java.util.Scanner;



import br.edu.fatecbauru.controllers.bridge.MasterRDImpl;
import br.edu.fatecbauru.controllers.util.InfoUtils;




import com.sun.jna.Native;

public class RFID {

	private MasterRDImpl dll = null;
	private short icdev = 0x0000;
	private int[] pTagType = new int[]{0};
	private char[] pSnr = new char[200];
	private byte[] plen = new byte[]{0};
	byte[] pSize=new byte[]{0};
	String hexStr = "";
	byte[] pData = new byte[100];
	//bloco atual
	int current_block = 60;
	
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
		System.out.println("Coloque o cartão proximo do leitor e pressione [ENTER] para continuar");
		Scanner s = new Scanner(System.in);
		s.nextLine();
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
		System.out.println("rf_request: icdev= " + icdev + ", model= " + model + ", ptagType=" + Arrays.toString(pTagType));
		return dll.rf_request(icdev, model, pTagType);
		
	}
	
	public int antiCollision(){
		System.out.println("rf_anticoll: icdev=" + icdev + ", bcnt=" + 4 + ", pSnr= " + Arrays.toString(pSnr) + ", plen=" + Arrays.toString(plen));
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
		System.out.println("rf_select: icdev=" + icdev + ", pSnr=" + Arrays.toString(pSnr) + ", snrLen=" + 4 + ", pSize=" +  Arrays.toString(plen));
		int retorno = dll.rf_select(icdev, pSnr, (byte) 4, plen);
		System.out.println(Arrays.toString(plen));
		return retorno;
	}
	
	public void redefineVariaveis(){
		pTagType = new int[]{0};
		pSnr = new char[200];
		plen = new byte[]{0};
		pSize=new byte[]{0};
		current_block = 60;
		pData = new byte[100];
	}
	public int autenticar(int bloco){
	
		byte model = 0x60; //Key A
		byte pKeyn = (byte)0xFF;
//		byte pKeyn2 = (byte)0xEE;
		byte[] pKey = new byte[]{(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255};
//		byte[] pKey = new byte[]{51, 48, 48, 48, 48, 48, 48};
		
		System.out.println("rf_m1_authentication2: icdev=" + icdev + ", model=" + model + ", bloco=" + bloco + ", pKey=" + Arrays.toString(pKey));
	    return dll.rf_M1_authentication2(icdev, (char) model, (byte) bloco, pKey);
	}
	
	public String lerInformacao(int bloco){
//		request();
//		antiCollision();
//		selecionar();
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
	
		
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
		

//		request();
//		antiCollision();
//		selecionar();
		
		if (valor.length() > 16){
			System.out.println("Texto excedeu 16 bytes: " + valor);
		}
		
//		String hexStr = "";
		hexStr = "";
		
		for (int i=0; i < 16; i++){
			if (i < valor.length()){
			pData[i] = ((byte) valor.charAt(i));
			}else{
				pData[i] = ((byte) 0);
			}
//			hexStr += Integer.toHexString((int) valor.charAt(i));
		}
//		
//		for (int i=0; i < (16 - valor.length()); i++){
//			hexStr += "00";
//		}
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
	
		//antes devo autenticar
		System.out.println("Auth: " + autenticar(bloco)); 
		
		//escrevendo a string no cartão
		System.out.println("rf_m1_write: icdev=" + icdev + ", bloco=" + bloco + ", valor=" + Arrays.toString(pData));
		
		
		
		return dll.rf_M1_write(icdev, (byte) bloco, pData);
	
	}
}
