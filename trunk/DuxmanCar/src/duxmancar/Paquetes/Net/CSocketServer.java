/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Paquetes.Net;

import duxmancar.lib.util.IDatosGenerales;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public abstract class CSocketServer extends Thread implements IDatosGenerales
{

    private BufferedReader m_bfInput = null;
    private BufferedWriter m_bfOutput = null;
    private String m_sDatosOutput;
    private String m_sDatosInput;
    protected CHiloEntrada m_hiloEntrada;

    protected Boolean m_bSalir;
    protected Boolean m_bConectado;
    protected Logger m_log;

    protected String m_sServerName;
    
    private List<String> m_listaMsg;

    public boolean compruebaFinal(String sDato)
    {
        boolean rtn = false;        
        int iIndexINI;
        int iIndexFIN;
        
        if(sDato.endsWith(FIN_DATOS) && sDato.startsWith(INI_DATOS))
            rtn = true;
        else
        {
            iIndexINI = sDato.indexOf(INI_DATOS);
            if( iIndexINI >= 0 )
            {
                iIndexFIN = sDato.indexOf(FIN_DATOS, iIndexINI);
                if(iIndexFIN >= 0)
                {
                    try 
                    {
                        String sDatoTemp = sDato.substring( iIndexINI, iIndexFIN );
                        addMensajeEntrada( sDatoTemp );
                        setDatosEntrada( sDato.substring(iIndexFIN+1), true );
                    }
                    catch (Exception ex) 
                    {
                       m_log.error(ex);
                    }
                    
                }
            }            
            
        }
        
        return rtn;
    }
   
    public abstract void  initServer();
    
    public void stopLectura()
    {
        m_hiloEntrada.interrupt();
    }
    
    public CSocketServer(String sServerName)
    {
        super(sServerName);
        m_bSalir = new Boolean(false);
        m_bConectado = new Boolean(false);
        m_sDatosInput = "";
        m_sDatosOutput = "";
        
        m_listaMsg = Collections.synchronizedList(new ArrayList<String>());
    }

    protected void creaBufferStream(InputStream input, OutputStream output) throws Exception
    {
        try
        {
            m_log.info("Creada entrada de fichero");
            m_bfInput = new BufferedReader(new InputStreamReader(input));
            m_hiloEntrada = new CHiloEntrada();

            m_log.info("Creada salida de fichero");
            m_bfOutput = new BufferedWriter(new OutputStreamWriter( output ) );

            m_hiloEntrada.start();
        }
        catch (Exception e)
        {
            m_log.error(e);
        }
    }

    public void setSalir(boolean bSalir)
    {
        synchronized (m_bSalir)
        {
            m_bSalir = bSalir;
        }
    }

    public boolean getSalir()
    {
        boolean rtn;
        synchronized (m_bSalir)
        {
            rtn = m_bSalir.booleanValue();
        }
        return rtn;
    }

    /**
     * @return the m_conectado
     */
    public Boolean getConectado()
    {
        boolean rtn;
        synchronized (m_bSalir)
        {
            rtn = m_bConectado.booleanValue();;

        }
        return rtn;
    }

    /**
     * @param m_conectado the m_conectado to set
     */
    public void setConectado(Boolean bConectado)
    {
        synchronized (m_bSalir)
        {
            m_bConectado = bConectado;
        }
    }

    public String getDatosEntrada()
    {
        String rtn = "";
        synchronized (m_sDatosInput)
        {
            rtn = m_sDatosInput;
            m_sDatosInput = "";
        }
        return rtn;
    }

    public void EsperaSalir() throws InterruptedException
    {
        Thread.sleep(100000);
    }

    public void setDatosEntrada(String sDato)
    {
        setDatosEntrada(sDato, false);
    }

    public void setDatosEntrada(String sDato, boolean bVaciar)
    {
        StringBuffer bf = new StringBuffer();
        synchronized (m_sDatosInput)
        {
            if (bVaciar)
            {
                bf.append(sDato);
            }
            else
            {
                bf.append(m_sDatosInput).append(sDato);
            }
            m_sDatosInput = bf.toString();
        }
    }

    public void setDatosSalida(String sDato)
    {
        try
        {
            m_bfOutput.write(sDato);
            m_bfOutput.newLine();
            m_bfOutput.flush();
        }
        catch (IOException ex)
        {
            m_log.fatal(ex);
        }
    }

    private synchronized void addMensajeEntrada(String sDato) throws Exception
    {        
        
        while (m_listaMsg.size() == MAX_MENSAJES )
        {
            wait();
        }
        m_log.info("Añadido MSG :"  +sDato );
        m_listaMsg.add(sDato);
        notify();        
    }
    
    public synchronized String getMensaje() throws Exception
    {  
        notify();
        while(m_listaMsg.size() == 0)
        {
            wait();
        }
        String sRtn = m_listaMsg.get(0);
        m_listaMsg.remove(0);
        return sRtn;                                       
    }

    protected class CHiloEntrada extends Thread
    {

        public CHiloEntrada()
        {
            super(m_sServerName + "_Entrada");
        }

        @Override public void run()
        {
            while (!getSalir())
            {
                try
                {
                    String sDatosTemp = m_bfInput.readLine();
                    setDatosEntrada(sDatosTemp);
                    if (compruebaFinal(sDatosTemp))
                    {
                        addMensajeEntrada(getDatosEntrada());
                    }
                }
                catch (Exception ex)
                {
                    m_log.error(ex);
                }
            }

        }
    }
}
