/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aduce
 */
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
    
  public CSockectControlServer(String sServerName, int iPuertoConexion )
  {
    super( sServerName );   
    m_iPuertoConexion = iPuertoConexion;
    
  }
  
          
  public CSockectControlServer(String sServerName,  InputStream input,  OutputStream output )
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
  
  
  public void initServer()
  {
    try
    {
      m_serverSocket  = new ServerSocket ( m_iPuertoConexion );
      m_socket        = new Socket();      
    }
    catch(Exception e)
    {
       e.printStackTrace ();
    }
  }
      
  
  public boolean ejecutar()
  {
    boolean rtn=true;
    try
    {     
      m_socket = m_serverSocket.accept ();
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
