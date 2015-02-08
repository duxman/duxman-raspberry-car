/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Paquetes.Net;

import java.util.Random;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public class CBtServer extends CSocketServer
{


    private UUID m_ServerUUID = null;
    private String m_sServerURL = "";
    private StreamConnectionNotifier m_localServer = null;
    private StreamConnection m_conexionCliente = null;
    private LocalDevice m_DispositivoLocal = null;    
    
    public CBtServer(String sServerName ) throws Exception
    {
        super(sServerName + "_hilo_principal");

        m_log = Logger.getRootLogger( );
        m_sServerName = sServerName;

        m_log.info("inicializamos datos BlueTooth");
        Random rand = new Random();
        int randomNum = rand.nextInt((9999 - 1) + 1) + 1;

        m_ServerUUID = new UUID(String.valueOf(randomNum), true);
        m_log.info("UUIID:" + m_ServerUUID.toString());

        m_sServerURL = "btspp://localhost:" + m_ServerUUID + ";name=" + m_sServerName;
        m_log.info("URL :" + m_sServerURL);     
         
        setConectado( Boolean.FALSE);
        setSalir( Boolean.FALSE );        
    } 

    @Override public void run()
    {
        while ( getSalir() == false )
        {
            try
            {               
                activaBT();
                
                m_log.info("Esperando conexion");
                m_conexionCliente = m_localServer.acceptAndOpen();                                                
                m_log.info("Comenzamos proceso");
                setConectado( Boolean.TRUE );
                
                while ( getSalir() == false )
                {                    
                    m_log.info("Cliente conectado, obtenemos conexiones");
                    creaBufferStream(m_conexionCliente.openInputStream(), m_conexionCliente.openOutputStream());

                    EsperaSalir();
                }
                
                setConectado( Boolean.FALSE );
            }
            catch (Exception ex)
            {
                try
                {
                    setConectado((Boolean) false);
                    m_conexionCliente.close();
                    m_localServer.close();
                    stopLectura();
                    m_log.error("Desconectamos BT");
                }
                catch (Exception ex1)
                {
                    m_log.error(ex1);
                }
            }
        }
    }

    public void activaBT()
    {
        try
        {
            m_log.info("Activamos BT");
            m_DispositivoLocal.setDiscoverable(DiscoveryAgent.GIAC);
            m_log.info("Iniciamos Listener");                                    
        }
        catch (Exception ex)
        {
            m_log.error(ex);
        }
    }   

    @Override  public void initServer()
    {
        try
        {
            m_log.info("Creamos dispositivo local");
            m_DispositivoLocal = LocalDevice.getLocalDevice();
            m_log.info("Creamos Servidor Local");
            m_localServer = (StreamConnectionNotifier) Connector.open(m_sServerURL);
            start();
        }
        catch (Exception ex)
        {
           m_log.error(ex);
        }
    }
   
}
