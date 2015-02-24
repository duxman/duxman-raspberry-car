/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Net;

import duxmancar.util.IDatosGenerales;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public class CSocketMulticast  extends CSocketServer implements IDatosGenerales
{
    int         m_iPuertoMulticast;
    InetAddress m_inetAddress;
    DatagramSocket m_serverSocket;
    Logger      m_log;
    String      m_sCadenaMensaje;
    public CSocketMulticast(String sServerName, int iPuerto) throws UnknownHostException
    {
        super( sServerName );
        m_log = Logger.getRootLogger();
        m_iPuertoMulticast  = iPuerto;
        m_inetAddress       = InetAddress.getLocalHost();
    }
        

    @Override  public void initServer()
    {
        try
        {
            m_log.info("Creamos Sockect multicast en " + m_inetAddress.getHostAddress() + ":" + m_iPuertoMulticast);        
            m_serverSocket = new DatagramSocket();
            start();
        }
        catch (SocketException ex)
        {
            m_log.error(ex.getMessage());
        }
    }
    
    public void setCadenaMensaje( String sCadenaMensaje )
    {
        m_sCadenaMensaje = sCadenaMensaje;
    }
    
    @Override  public void run()
    {
        int i=0;
        while( getSalir() == false && i < NUM_REPETICIONES_MULTICAST )
        {
            try
            {
                DatagramPacket paquete =  new DatagramPacket(   m_sCadenaMensaje.getBytes() ,
                                                                m_sCadenaMensaje.getBytes().length ,
                                                                m_inetAddress,
                                                                m_iPuertoMulticast);
                m_serverSocket.send( paquete );
                m_log.info( "Mandamos paquete multicast " + m_sCadenaMensaje );
                i++;
                EsperaSalir();
            }
            catch (Exception ex)
            {
                m_log.error( ex.getMessage() );
            }
        }
    }
}
