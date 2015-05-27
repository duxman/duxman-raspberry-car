/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Datos;

import static duxmancar.util.IDatosGenerales.MAX_MENSAJES;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public class CListaDatosProvider
{
    
    private static CListaDatosProvider instance = null;
    
    private List<String> m_listaMsg;
    
    Logger m_log;
    
    private CListaDatosProvider()
    {
        m_log = Logger.getRootLogger();
        m_listaMsg = Collections.synchronizedList(new ArrayList<String>());    
    }
    
    public static  CListaDatosProvider getInstance()
    {
        if( instance == null )
        {
            instance = new CListaDatosProvider();
        }
        
        return instance;
    }
    
    public synchronized void addMensajeEntrada(String sDato) throws Exception
    {

        while (m_listaMsg.size() == MAX_MENSAJES)
        {
            wait();
        }
        m_log.info("Añadido MSG :" + sDato);
        m_listaMsg.add(sDato);
        notify();
    }
     
    public synchronized String getMensaje() throws Exception
    {
        notify();
        while (m_listaMsg.size() == 0)
        {
            wait();
        }
        String sRtn = m_listaMsg.get(0);
        m_listaMsg.remove(0);
        return sRtn;
    }
    
    
    
}
