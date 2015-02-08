package duxman.lib.net;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aduce
 */
import duxman.lib.log.CLog;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CSockectControlServer extends CSockectControl
{
  private int                   m_iPuertoConexion; 
  private ServerSocket          m_serverSocket;
  private Socket                m_socket;
  private String                m_sUsuarioConectado;
    
  public CSockectControlServer(String sServerName, int iPuertoConexion, CLog log )
  {
    super( sServerName, log );   
    m_iPuertoConexion = iPuertoConexion;
    
  }
  
          
  public CSockectControlServer(String sServerName, CLog log )
  {
    super( sServerName, log );
    try
    {      
  
      m_iPuertoConexion = 0;     
    }
    catch (Exception ex)
    {
      m_log.excepcion(ex);
    }
  }
   
  
  public void initServer()
  {
    try
    {
      m_serverSocket  = new ServerSocket ( m_iPuertoConexion );
      m_socket        = new Socket();           
      CNetListener netListener = new CNetListener(m_sServerName);
      netListener.start();
    }
    catch(Exception e)
    {
      m_log.excepcion(e);
    }
  }
      
  
  @Override  public boolean ProcesoEjecucion()
  {
    boolean rtn=true;
    try
    {                 
      setSalir ( false );      
      while( condicionSalida() )       
      {
         EsperaSalir ();
      }
      
    }
    catch (Exception ex)
    {
      m_log.excepcion(ex);
    }
    return rtn;
  }
  
  private boolean condicionSalida()
  {
    boolean rtn;
    rtn = m_socket.isConnected () && !( getSalir () ); 
    return rtn;
  }
  
  class CNetListener extends Thread
  {
      public CNetListener(String sServerName)
      {
          super(sServerName);          
      }
      
      @Override  public void run()
      {
          try
          {
              synchronized(m_socket)
              {
                m_socket = m_serverSocket.accept ();
                m_sUsuarioConectado = m_socket.getRemoteSocketAddress ().toString ();
                creaBufferStream( m_socket );
                Run();
              }
          }
          catch (Exception ex)
          {
              Logger.getLogger(CSockectControlServer.class.getName()).log(Level.SEVERE, null, ex);
          }
      }
              
      
  }
  
  public abstract boolean compruebaFinal(String sDato);
  public abstract boolean ProcesaDatos(String sDato);
      
}
