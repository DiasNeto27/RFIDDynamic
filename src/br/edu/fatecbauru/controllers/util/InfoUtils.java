package br.edu.fatecbauru.controllers.util;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * Esta classe cont�m informa��es uteis para toda a aplica��o, como o nome de todos os contribuintes,
 * vers�o da aplica��o, nome do projeto e etc.
 * @author Dias
 *
 */
public class InfoUtils {		
	/** Inicializa o nome do projeto */
	private static String projectName = "RFID Dynamic";
	/** Inicializa a informa��o da vers�o do projeto */
	private static String version = "1.0";
	/** Inicializa o nome de cada colaborador do projeto */
	private static String authors[] = {"Jos� Dias Neto", "Juliane Oliveira", "Felipe da Cunha", "Juliano Torres"};
	/** Inicializa o nome de cada coordenador do projeto*/
	private static String coordinators[] = {"Gustavo Bruschi", "Luiz Alexandre"};
		
	/** Inicializa um {@code TreeMap} contendo o v�nculo entre os c�digos de erros e a sua
	 * representa��o de forma escrita */
	private static Map<Integer,String> errors = new TreeMap<Integer, String>(){
		{
			put(1, "Baud rate error");
			put(2, "Port error or Disconnect");
			put(10, "General error");
			put(11, "undefined");
			put(12, "Command Parameter error");
			put(13, "No card");
			put(20, "Request failure");
			put(21, "Reset failure");
			put(22, "Authenticate failure");
			put(23, "Read block failure");
			put(24, "Write block failure");
			put(25, "Write address failure");
			put(26, "Write adress failure");
		}
	};
	
	/**
	 * Retorna a descri��o do erro de acordo com o c�digo que foi passado
	 * 
	 * @param errorCode O c�digo do erro 
	 * @return retorna uma {@code String} que representa a descri��o do erro de acordo com o c�digo informado 
	 */
	public static String getErrorMessage(int errorCode){
		return errors.get(errorCode);
	}
	
	/**
	 * Retorna o nome atual do projeto
	 * @return retorna uma {@code String} com a representa��o do nome do projeto
	 */
	public static String getProjectName(){
		return projectName;
	}
	
	/**
	 * Retorna a vers�o atual do projeto
	 * 
	 * @return retorna uma {@code String} com a representa��o da vers�o do projeto
	 */
	public static String getVersion(){
		return version;
	}
	
	/**
	 * Retorna os autores do projeto de forma j� formatada 
	 * pronto para imprimir na tela
	 * 
	 * @return retorna uma {@code String} contendo o nome de todos os autores do projeto 
	 */
	public static String getAuthors(){
		 return getFormattedNames(authors);
	}
	
	/**
	 * Retorna os coordenadores do projeto de forma j� formatada
	 * pronto para imprimir na tela
	 * 
	 * @return retorna uma {@code String} contendo o nome de todos os coordenadores do projeto
	 */
	public static String getCordinators(){
		 return getFormattedNames(coordinators);
	}
	
	/**
	 * Retorna de forma formatada com conectivos "," e "e" todos os nomes em ordem alfab�tica
	 * de modo que estejam prontos para ser apresentado ao usu�rio
	 * 
	 * @param names Um array de strings contendo o nome de todos no qual se deseja formatar 
	 * @return retorna uma {@code String} contendo todos os nomes recebidos em ordem alfab�tica
	 */
	public static String getFormattedNames(String[] names){
		 String result = "";		
		 Arrays.sort(names);
		 for (int i = 0; i < names.length; i++){
			 if (i==names.length - 1){
				 result += " e ";
			 }else if (i > 0){				 
				 result += ", ";
			 }
			 result += names[i];
		 }
		 
		 return result;	
	}
	
	
	
}
