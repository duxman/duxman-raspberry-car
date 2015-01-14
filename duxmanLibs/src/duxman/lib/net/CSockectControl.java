package duxman.lib.net;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import duxman.lib.util.CSubProceso;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aduce
 */
public abstract class CSockectControl extends CSubProceso
{
  public    BufferedReader        m_bfInput = null;
  public    BufferedWriter        m_bfOutput = null;
  private   String                m_sDatosOutput;
  private   String                m_sDatosInput;
  protected String                m_sServerName;
  protected CHiloEntrada          m_hiloEntrada;
  protected CHiloSalida           m_hiloSalida;
  private   Boolean               m_bSalir;
  
  public CSockectControl(String sServerName)
  {
    super(sServerName+"_Hilo_Principal");
    m_sServerName = sServerName;
    m_sDatosInput="";    
    m_sDatosOutput="";
    m_bSalir = false;
  }
  
  public abstract boolean compruebaFinal(String sDato);
  public abstract boolean ProcesaDatos(String sDato);
    
  public void setSalir(boolean  bSalir)
  {
    synchronized(m_bSalir)
    {
      m_bSalir=bSalir;
      m_bSalir.notifyAll ();
    }
  }
    
  public boolean getSalir()
  {
    boolean rtn;
    synchronized(m_bSalir)
    {
      rtn = m_bSalir.booleanValue ();
    }
    return rtn;
  }
  
  public String getDatosEntrada()
  {
    String rtn="";
    synchronized(m_sDatosInput)
    {
      rtn = m_sDatosInput;  
      m_sDatosInput="";
    }
    return rtn;
  }  
  
  public void EsperaSalir() throws InterruptedException
  {    
    m_bSalir.wait(100000);
  }
  
  public void  setDatosEntrada(String sDato)
  {
    StringBuffer bf = new StringBuffer ();
    synchronized(m_sDatosInput)
    {
      bf.append (m_sDatosInput).append (sDato);
      m_sDatosInput = bf.toString ();      
    }    
  }  
  
  public void setDatosSalida(String sDato)
  {
    StringBuffer bf = new StringBuffer ();
    synchronized(m_sDatosOutput)
    {
      bf.append (m_sDatosOutput).append (sDato);     
      m_sDatosOutput = bf.toString ();
      m_sDatosOutput.notifyAll ();
    }           
  }
 
  public void vaciaDatosSalida()
  {
    synchronized(m_sDatosOutput)
    {
      m_sDatosOutput="";
    }   
  }
  
  protected void creaBufferStream(Socket sockect ) throws Exception
  {
      m_bfInput  = new BufferedReader ( new InputStreamReader ( sockect.getInputStream  () ) );
      m_hiloEntrada = new CHiloEntrada ();
      
      m_bfOutput = new BufferedWriter ( new OutputStreamWriter( sockect.getOutputStream () ) );        
      m_hiloSalida = new CHiloSalida ();
      
      m_hiloEntrada.start ();
      m_hiloSalida.start ();
      
  }
   
  protected void creaBufferStream(InputStream input,  OutputStream output ) throws Exception
  {
      m_bfInput  = new BufferedReader ( new InputStreamReader ( input ) );
      m_hiloEntrada = new CHiloEntrada ();
      
      m_bfOutput = new BufferedWriter ( new OutputStreamWriter( output ) );        
      m_hiloSalida = new CHiloSalida ();
      
      m_hiloEntrada.start ();
      m_hiloSalida.start ();
      
  }
  
  protected class CHiloEntrada extends Thread
  {
     public CHiloEntrada()
    {
      super( m_sServerName+"_Entrada" );              
    }
            
    @Override public void run()
    { 
      while(!getSalir())
      {
        try
        {
          m_sDatosOutput.wait(); 
          String sDatosTemp = m_bfInput.readLine ();
          setDatosSalida (sDatosTemp);
          if( compruebaFinal( sDatosTemp ) )
          {
            ProcesaDatos( getDatosEntrada () );
          }          
        }
        catch (Exception ex)
        {
          Logger.getLogger (CSockectControlServer.class.getName()).log (Level.SEVERE, null, ex);
        }
      }
      
    }
  }
  
  protected class CHiloSalida extends Thread 
  {
    public CHiloSalida()
    {
      super( m_sServerName+"_Salida" );              
    }
            
    @Override public void run()
    { 
      while(!getSalir())
      {
        try
        {
          m_sDatosOutput.wait(); 
          m_bfOutput.write (m_sDatosOutput);
          vaciaDatosSalida ( );
        }
        catch (Exception ex)
        {
          Logger.getLogger (CSockectControlServer.class.getName()).log (Level.SEVERE, null, ex);
        }
      }
      
    }
    
  }
}
