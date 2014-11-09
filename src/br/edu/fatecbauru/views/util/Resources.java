package br.edu.fatecbauru.views.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Esta classe contém recursos úteis para toda a aplicação em console, como
 * limpar a tela de acordo com o sistema operacional do usuário
 * 
 * @author Dias
 *
 */
public class Resources {

	public final static char OPCAO_SIM = 'S';
	public final static char OPCAO_NAO = 'N';
	
	

	/**
	 * Limpa o console de acordo com o sistema operacional utilizado
	 */
	public static void clearScreen() {
		String os = System.getProperty("os.name");
		try {
			if (os.contains("Windows")) {
				Runtime.getRuntime().exec("cls");
			} else {
				Runtime.getRuntime().exec("clear");
			}
		} catch (Exception err) {
		}
	}

	public static String getOpcoesFormatadas() {
		return "(" + OPCAO_SIM + "/" + OPCAO_NAO + ")";
	}

	public static void aguardar(Integer segundos){
		try {
			Thread.sleep(segundos * 1000);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}
	}
	
	
	private static final Locale BRAZIL = new Locale("pt","BR");    
	private static final DecimalFormatSymbols REAL = new DecimalFormatSymbols(BRAZIL);    
	public static final DecimalFormat DINHEIRO_REAL = new DecimalFormat("¤ ###,###,##0.00",REAL);    

}
