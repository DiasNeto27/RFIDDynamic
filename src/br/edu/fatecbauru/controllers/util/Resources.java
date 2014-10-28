package br.edu.fatecbauru.controllers.util;

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

	public static byte[] fromHexString(String s, int offset, int length) {
		if ((length % 2) != 0)
			return null;

		byte[] byteArray = new byte[length / 2];

		int j = 0;
		int end = offset + length;
		for (int i = offset; i < end; i += 2) {
			int high_nibble = Character.digit(s.charAt(i), 16);
			int low_nibble = Character.digit(s.charAt(i + 1), 16);

			if (high_nibble == -1 || low_nibble == -1) {
				return null;
			}

			byteArray[j++] = (byte) (((high_nibble << 4) & 0xf0) | (low_nibble & 0x0f));
		}
		return byteArray;
	}

}
