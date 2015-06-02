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
public class CCirculos
{
    public static int POSICION_IZQ = 0;
    public static int POSICION_CEN = 1;
    public static int POSICION_DER = 2;
    
    public static int PIXEL_IZQ = 106;
    public static int PIXEL_CEN = 214;
    public static int PIXEL_DER = 320;
    
    private static boolean[] m_circulos = {false,false,false};
        
    public static void setCirculo(boolean izq, boolean cen, boolean der) 
    {
        synchronized(m_circulos)
        {
            m_circulos[POSICION_IZQ] = izq;
            m_circulos[POSICION_DER] = der;
            m_circulos[POSICION_CEN] = cen;
        }
    }
    
    public static boolean  hayCirculoCentro()
    {
        return m_circulos[POSICION_CEN];
    }
    
    public static boolean  hayCirculoDerecha()
    {
        return m_circulos[POSICION_DER];
    }
    
    public static boolean  hayCirculoIzquierda()
    {
        return m_circulos[POSICION_IZQ];
    }
    
    public static String texto()
    {        
        return "DERECHA : " + ( (m_circulos[POSICION_DER]) ? "SI": "NO" ) + " CENTRO : " + ( (m_circulos[POSICION_CEN]) ? "SI": "NO" ) + " IZQUIERDA : " + ( (m_circulos[POSICION_IZQ]) ? "SI": "NO" );
    }
}
