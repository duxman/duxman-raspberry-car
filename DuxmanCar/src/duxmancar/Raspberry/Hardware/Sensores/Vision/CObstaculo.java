/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Raspberry.Hardware.Sensores.Vision;

/**
 *
 * @author duxman
 */
public class CObstaculo
{
    public static int POSICION_IZQ = 0;
    public static int POSICION_CEN = 1;
    public static int POSICION_DER = 2;
    
    public static int ANCHO_IMG = 240;
    public static int ALTO_IMG = 180;
    public static int PIXEL_IZQ = 10;
    public static int PIXEL_CEN = ANCHO_IMG-10;
    public static int PIXEL_DER = ANCHO_IMG;
    public static enum eSimbolo 
    {
        DERECHA,IZQUIERDA,PARO,ATRAS,NONE,OTRO;
        
        @Override public String toString()
        {
            String rtn = "NONE";
            switch(this)
            {
                case DERECHA:
                {
                    rtn = "DERECHA";
                    break;
                }
                case IZQUIERDA:
                {
                    rtn = "IZQUIERDA";
                    break;
                }
                case PARO:
                {
                    rtn = "PARO";
                    break;
                }
                case ATRAS:
                {
                    rtn = "ATRAS";
                    break;
                }
                case OTRO:
                {
                    rtn = "OTRO";
                    break;
                }
                    
            }
            return rtn;
        }
    };
    
    private static eSimbolo[] m_obstaculos = {eSimbolo.NONE,eSimbolo.NONE,eSimbolo.NONE};
        
    public static void setObstaculo() 
    {
        synchronized(m_obstaculos)
        {
            m_obstaculos[POSICION_IZQ] = eSimbolo.NONE;
            m_obstaculos[POSICION_DER] = eSimbolo.NONE;
            m_obstaculos[POSICION_CEN] = eSimbolo.NONE;
        }
    }
    
    public static void setObstaculo(eSimbolo izq, eSimbolo cen, eSimbolo der) 
    {
        synchronized(m_obstaculos)
        {
            m_obstaculos[POSICION_IZQ] = izq;
            m_obstaculos[POSICION_DER] = der;
            m_obstaculos[POSICION_CEN] = cen;
        }
    }
    
    public static eSimbolo  hayObstaculoCentro()
    {
        return m_obstaculos[POSICION_CEN];
    }
    
    public static eSimbolo  hayObstaculoDerecha()
    {
        return m_obstaculos[POSICION_DER];
    }
    
    public static eSimbolo  hayObstaculoIzquierda()
    {
        return m_obstaculos[POSICION_IZQ];
    }
    
    public static String texto()
    {        
        return "DERECHA : " + ( (m_obstaculos[POSICION_DER]!= eSimbolo.NONE) ? m_obstaculos[POSICION_DER].toString() : "NO" ) + " CENTRO : " + ( (m_obstaculos[POSICION_CEN]!= eSimbolo.NONE) ? m_obstaculos[POSICION_CEN].toString(): "NO" ) + " IZQUIERDA : " + ( (m_obstaculos[POSICION_IZQ]!= eSimbolo.NONE) ? m_obstaculos[POSICION_IZQ].toString() : "NO" );
    }
}
