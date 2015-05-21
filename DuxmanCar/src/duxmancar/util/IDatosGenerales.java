/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.util;

/**
 *
 * @author duxman
 */
public interface IDatosGenerales
{   
    //Datos Generales
    static int NINGUNO = -1;
    
    static int NUM_REPETICIONES_MULTICAST = 150;
    
    //Datos conexion
    static enum TIPO_CONEXION { BLUE, NET, WS };
    
    //Datos de Mensajes
    static int  MAX_MENSAJES = 10;
    public static String INI_DATOS = "#INI#";
    public static String FIN_DATOS = "#FIN#";
    public static int POS_ID = 0;
    public static int POS_ORIGEN = 1;
    public static int POS_DESTINO = 2;
    public static int POS_ACCION = 3;
    public static int POS_PARAMETROS = 4;
    public static int LEN_PARAMETROS_FIJOS = 4;
    public static String DIVISOR_PARAMETROS = "|";

    //DatosMensajes
    public static enum eAccionesCon     
    {   
        CONECTAR    (0), 
        DESCONECTAR (1),
        DAME        (2),
        PON         (3),
        MANDAR      (4);
        
        private final int m_value;
        private eAccionesCon(int value)
        {
            m_value=value;
        };                
    };
    public static enum eAccionesServo   
    {
        DERECHA     (0),
        IZQUIERDA   (1),
        ARRIBA      (2),
        ABAJO       (3),
        STOP        (4);
        private final int m_value;
        private eAccionesServo(int value)
        {
            m_value=value;
        };                
        
    };
    public static enum eAccionesled     
    {
        ONROJO(0),OFFROJO(1),ONVERDE(2),OFFVERDE(3),ONAZUL(4),OFFAZUL(5),ALLOFF(6),ALLON(7);
        private final int m_value;
        private eAccionesled(int value)
        {
            m_value=value;
        };                
    };
    public static enum eAccionesCam     
    {
        BUSCAR(0),SEGUIR(1),OFFBUSCAR(2),OFFSEGUIR(3);
        private final int m_value;
        private eAccionesCam(int value)
        {
            m_value=value;
        }; 
        
    }
    public static enum eEstadoDato	
    {
        NEW(0),CURRENT(1),OLD(2);
        private final int m_value;
        private eEstadoDato(int value)
        {
            m_value=value;
        };         
    };
    public static enum eDestinos	
    {
        RASPBERRY(0),CONTROLADOR(1),SERVOCONTROL(2),LEDCONTROL(3),CAMCONTROL(4),DCCONTROL(5);
        private final int m_value;
        private eDestinos(int value)
        {
            m_value=value;
        }; 
    };
    
}
