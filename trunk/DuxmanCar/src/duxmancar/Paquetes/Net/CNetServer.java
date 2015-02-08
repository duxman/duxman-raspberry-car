/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Paquetes.Net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public class CNetServer extends  CSocketServer
{
    
    ServerSocket m_server;
    Socket       m_socket;
    int          m_iPuerto;
    
    public CNetServer( String sServerName, int iPuertoConexion) throws Exception
    {
        super( sServerName );            
        m_log = Logger.getRootLogger();
        m_iPuerto = iPuertoConexion;
        
        
    }
    
    @Override  public void initServer()
    {
        try
        {
            m_log.info("Creamos Servidor Local en puerto " + String.valueOf(m_iPuerto));
            m_server = new ServerSocket(m_iPuerto);
            start();
        }
        catch (IOException ex)
        {
           m_log.error(ex);
        }
    }
    
    @Override   public void run()
    {
        while ( getSalir() == false )
        {
            try
            {
                m_log.info("Esperando conexion en puerto " + String.valueOf(m_iPuerto)); 
                m_socket = m_server.accept();
                m_log.info("Comenzamos proceso Socket Red");
                setConectado( Boolean.TRUE );
                
                while ( getSalir() == false )
                {                    
                    m_log.info("Cliente conectado, obtenemos conexiones de red");
                    creaBufferStream(m_socket.getInputStream(), m_socket.getOutputStream());

                    EsperaSalir();
                }
                
                setConectado( Boolean.FALSE );
            }
            catch (Exception ex)
            {
                try
                {
                    setConectado((Boolean) false);
                    m_server.close();
                    m_socket.close();
                    stopLectura();
                    m_log.error("Desconectamos Socket");
                }
                catch (Exception ex1)
                {
                    m_log.error(ex1);
                }
            }
        } 
        
    }       
    
}
