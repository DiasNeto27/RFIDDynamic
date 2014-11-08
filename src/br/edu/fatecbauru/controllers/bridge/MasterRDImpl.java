package br.edu.fatecbauru.controllers.bridge;


import com.sun.jna.Library;

/**
 * Esta interface faz a ponte entre os métodos existentes na DLL <i>MasterRD.dll</i> e o <i>Java</i>
 * @author Dias
 *
 */

public interface MasterRDImpl extends Library {
   public int rf_init_com(int port, long baud);
   
   public int rf_ClosePort();
   
   public int rf_request(short icdev, byte model, int[] pTagType);
   
   public int rf_anticoll(short icdev,char bcnt,char[] pSnr,byte[] plen);
   
   public int rf_select(short icdev,char[] pSnr, byte snrLen,byte[] pSize);
   
   public int rf_M1_authentication2(short icdev,char model,byte block,byte[] pKey);
   
   public int rf_M1_write(short icdev,byte block,byte pData[]);
   
   public int rf_M1_read(short icdev, byte block,byte[] pData,byte[] dataLen);
   
   public int rf_light(short icdev, byte color);
   
   public int rf_beep(short icdev, byte msec);
   
}
 