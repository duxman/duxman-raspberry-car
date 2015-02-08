package duxman.lib.util;

public interface CDatosComunes 
{
	public static enum eAcciones	{CONN,DESC,DER,IZQ,ARR,ABA,BUSCAR,DAME,PON,SEND};
	public static enum eEstados		{STOP,STOPING,START,STARTING,OFF,RUN,PAUSE};
	public static enum eComponentes	{COMP,MOVI,BLUE,WIFI,AUTO,CAM};
	public static enum eDirecciones {INPUT,OUTPUT,NONE }
	public static enum eEstadoDato	{NEW,CURRENT,OLD};
	public String 	DireccionLocal 	= "";
	public String 	DireccionRemota	= "";
	public int 	  	PuertoLocal		= 0;
	public int		PuertoRemoto	= 0;
	public int 		UltimoId		= 0;
	public static String DIVISOR_PARAMETROS = "|";
        public static String INI_DATOS = "#INI#";
        public static String FIN_DATOS = "#FIN#";
	public static int POS_ID = 0;
	public static int POS_ORIGEN = 1;
	public static int POS_DESTINO = 2;
	public static int POS_ACCION = 3;
	public static int POS_PARAMETROS = 4;
	public static int LEN_PARAMETROS_FIJOS = 4;
	public static int TIEMPO_ESPERA_HILO = 10000;        	
}
