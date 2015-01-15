package duxman.lib.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aduce
 */
public abstract class CSockectControlClient extends CSockectControl
{
  private int                   m_iPuertoConexion;   
  private String                m_sUrlServidor;   
  private Socket                m_socket; 
  private String                m_sUsuarioConectado;
  
  public CSockectControlClient(String sServerName,String sUrlServidor, int iPuertoConexion )
  {
    super(sServerName);
    
    m_iPuertoConexion = iPuertoConexion;
    m_sUrlServidor    = sUrlServidor;    
  }
            
  public CSockectControlClient(String sServerName,  InputStream input,  OutputStream output )
  {
    super( sServerName );
    try
    {      
      creaBufferStream(input, output);
      m_iPuertoConexion = 0;     
    }
    catch (Exception ex)
    {
      Logger.getLogger (CSockectControlServer.class.getName()).log (Level.SEVERE, null, ex);
    }
  }
  
   public void initClient()
  {
    try
    {
      
      m_socket = new Socket(m_sServerName,m_iPuertoConexion);      
      creaBufferStream (m_socket);
    }
    catch(Exception e)
    {
       e.printStackTrace ();
    }
  }
   
  @Override  public boolean ProcesoEjecucion()
  {
    boolean rtn=true;
    try
    {     
      
      m_sUsuarioConectado = m_socket.getRemoteSocketAddress ().toString ();
      creaBufferStream( m_socket );
      setSalir ( false );
      
      while( condicionSalida() )       
      {
         EsperaSalir ();
      }
      
    }
    catch (Exception ex)
    {
      Logger.getLogger (CSockectControlServer.class.getName()).log (Level.SEVERE, null, ex);
    }
    return rtn;
  }  
  
  private boolean condicionSalida()
  {
    boolean rtn;
    rtn = m_socket.isConnected () && !( getSalir () ); 
    return rtn;
  }
  
  public abstract boolean compruebaFinal(String sDato);
  public abstract boolean ProcesaDatos(String sDato);
  
 
  
}
