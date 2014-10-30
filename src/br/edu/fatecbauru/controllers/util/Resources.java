package br.edu.fatecbauru.controllers.util;

/**
 * Esta classe cont�m recursos �teis para toda a aplica��o em console, como
 * limpar a tela de acordo com o sistema operacional do usu�rio
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

	

}
