/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Raspberry.Software;

import com.pi4j.io.gpio.GpioController;
import duxmancar.Automatico.CConduccionAutonoma;
import duxmancar.Datos.CDato;
import duxmancar.Datos.CListaDatosProvider;
import duxmancar.Datos.Procesadores.CPuenteHControl;
import duxmancar.Datos.Procesadores.CServoControl;
import duxmancar.Net.CBtServer;
import duxmancar.Net.CNetServer;
import duxmancar.Raspberry.Hardware.ControlMotores.CGestorI2CAdafruit;
import duxmancar.util.IDatosGenerales;
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

    private Boolean bFin;
    private CBtServer BtServer;
    private CNetServer NetServer;
    private Logger m_log;
    private List<String> m_listaMsg;
    private int SistemaConectado;
    private CServoControl servoControl;
    private CPuenteHControl dcControl;
    private GpioController m_gpio;
    private CListaDatosProvider m_listaDatosProvider;
    private CConduccionAutonoma m_automatico;

    public void create() throws Exception
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
        
        m_listaDatosProvider = CListaDatosProvider.getInstance();
        m_log.info("Obtenemos instancia del proveedor de datos");
        
        m_automatico  = new CConduccionAutonoma();
                
    }

    public CDuxmanCar(GpioController gpio) throws Exception
    {
        create();
        dcControl = new CPuenteHControl(gpio);                

    }

    public CDuxmanCar(CGestorI2CAdafruit oControladorServos) throws Exception
    {
        create();
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
        dcControl.start();       
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
                    ex.printStackTrace();
                }
                compruebaConexion();
            }
            else
            {
                m_log.info("Sin conexión comprobamos ");
                if ( NetServer.getConectado() )
                {
                    m_log.info("Conectado Sockect");
                    SistemaConectado = TIPO_CONEXION.NET.ordinal();
                }
                else if( BtServer.getConectado() )
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

        if ( SistemaConectado != NINGUNO )
        {
            msg = m_listaDatosProvider.getMensaje();            
        }

        if (msg.isEmpty() == false)
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
        else if (Destino == eDestinos.DCCONTROL.ordinal())
        {
            m_log.info("Enviamos a DCControl");            
            dcControl.addDato(dato);
        }
        else if( Destino ==  eDestinos.AUTO.ordinal() )
        {
            m_log.info("Enviamos a Control Automatico");            
            if( dato.getAccion() == eAccionesCon.CONECTAR.ordinal() )
            {
                m_automatico.setSalir( false );
                m_automatico.start();
            }
            else if( dato.getAccion() == eAccionesCon.DESCONECTAR.ordinal() )
            {
                m_automatico.setSalir( true );                
            }                        
        }
        return rtn;
    }

}
