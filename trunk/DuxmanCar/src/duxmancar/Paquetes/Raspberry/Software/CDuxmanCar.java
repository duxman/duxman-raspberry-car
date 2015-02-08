/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Paquetes.Raspberry.Software;

import duxmancar.Paquetes.Datos.CDato;
import duxmancar.Paquetes.Datos.Procesadores.CServoControl;
import duxmancar.Paquetes.Net.CBtServer;
import duxmancar.Paquetes.Net.CNetServer;
import duxmancar.Paquetes.Raspberry.Hardware.CGestorI2CAdafruit;
import duxmancar.lib.util.IDatosGenerales;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public class CDuxmanCar extends Thread implements IDatosGenerales
{

    private final Boolean bFin;
    private final CBtServer BtServer;
    private final CNetServer NetServer;
    private final Logger m_log;
    private List<String> m_listaMsg;
    private int SistemaConectado;
    private CServoControl servoControl;

    public CDuxmanCar(CGestorI2CAdafruit oControladorServos) throws Exception
    {
        m_log = Logger.getRootLogger();

        m_log.info("Creamos Bluetooth Server");
        BtServer = new CBtServer("ServidorBluetooth Raspbery");

        m_log.info("Creamos NET Server");
        NetServer = new CNetServer("ServidorSocket Raspberry", 15000);

        bFin = new Boolean(false);

        m_log.info("Creamos lista Sincronizada");
        m_listaMsg = Collections.synchronizedList(new ArrayList<String>());

        m_log.info("Iniciamos conexión a NINGUNO");
        SistemaConectado = NINGUNO;

        m_log.info("Creamos Controlador Servo");
        servoControl = new CServoControl(oControladorServos);
    }

    public void init()
    {
        m_log.info("Iniciamos Bluetooth");
        BtServer.initServer();
        m_log.info("Iniciamos Sockect");
        NetServer.initServer();
        m_log.info("Iniciamos Runner");
        this.start();
    }

    @Override public void run()
    {
        String msg = "";
        while (!bFin)
        {
            if (SistemaConectado != NINGUNO)
            {
                try
                {
                    m_log.info("Obtenemos datos");
                    obtenerDato();
                }
                catch (Exception ex)
                {
                    m_log.error(ex);
                }
                compruebaConexion();
            }
            else
            {
                m_log.info("Sin conexión comprobamos ");
                if (NetServer.getConectado())
                {
                    m_log.info("Conectado Sockect");
                    SistemaConectado = TIPO_CONEXION.NET.ordinal();
                }
                else
                {
                    m_log.info("Conectado Bluetooth");
                    SistemaConectado = TIPO_CONEXION.BLUE.ordinal();
                }
            }
        }
    }

    public void compruebaConexion()
    {
        m_log.info("Comprobamos conexión");
        if (SistemaConectado == TIPO_CONEXION.BLUE.ordinal())
        {
            m_log.info("Comprobamos bluetooth");
            if (!BtServer.getConectado())
            {
                SistemaConectado = NINGUNO;
                m_log.info("Desconectado Bluetooth");
            }
        }
        else if (SistemaConectado == TIPO_CONEXION.NET.ordinal())
        {
            m_log.info("Comprobamos Sockect");
            if (!NetServer.getConectado())
            {
                SistemaConectado = NINGUNO;
                m_log.info("Desconectado Socket");
            }
        }
    }

    public String obtenerDato() throws Exception
    {
        String msg = "";

        if (SistemaConectado == TIPO_CONEXION.BLUE.ordinal())
        {
            msg = BtServer.getMensaje();
            m_log.info("Dato Bluetooth : " + msg);
        }
        else if (SistemaConectado == TIPO_CONEXION.NET.ordinal())
        {
            msg = NetServer.getMensaje();
            m_log.info("Dato Sockect : " + msg);
        }

        if (!msg.isEmpty())
        {
            procesaDatos(msg);
            msg = "";
        }

        return msg;
    }

    public boolean procesaDatos(String sDato) throws Exception
    {
        boolean rtn = true;
        CDato dato = new CDato(sDato);
        int Destino = dato.getDestino();
        m_log.info("Procesamos : " + dato.toString());
        if (Destino == eDestinos.SERVOCONTROL.ordinal())
        {
            m_log.info("Enviamos a servoControl");
            servoControl.addDato(dato);
        }

        return rtn;
    }

}
