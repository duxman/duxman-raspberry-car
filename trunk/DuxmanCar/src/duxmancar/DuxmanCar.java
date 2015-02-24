/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar;

import com.pi4j.io.i2c.I2CBus;
import duxmancar.Raspberry.Hardware.CGestorI2CAdafruit;
import duxmancar.Raspberry.Software.CDuxmanCar;
import duxmancar.log.CLog;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public class DuxmanCar
{

    /**
     * @param args the command line arguments
     */
    static enum ESTADO_SERVICIO
    {

        START, STOP
    };

    static CLog claselog;
    static ESTADO_SERVICIO m_estado;
    static Logger m_log;

    public static I2CBus bus;
    public static CGestorI2CAdafruit oControladorServos;
    public static CProperties DuxmanCarProperties = null;

    public static void main(String[] args)
    {
        try
        {
            DuxmanCarProperties = new CProperties("DuxmanCar.properties");

            m_estado = ESTADO_SERVICIO.START;

            claselog = new CLog(false, CProperties.FICHERO_LOG, CProperties.MAX_SIZE_LOG, CProperties.MAX_FILES_LOG);
            m_log = Logger.getRootLogger();

            oControladorServos = new CGestorI2CAdafruit(CProperties.PUERTO_CONTROLADOR_SERVOS, CProperties.FREQ_CONTROLADOR_SERVOS);

            m_log.info("Inicio Aplicacion");
            CDuxmanCar duxmanCar = new CDuxmanCar(oControladorServos);
            duxmanCar.init();

            m_log.info("Creada clase principal");

            while (m_estado.equals(ESTADO_SERVICIO.START))
            {
                try
                {
                    Thread.sleep(50000);
                }
                catch (Exception ex)
                {
                    m_log.error(ex.getMessage());
                }
            }
            // TODO code application logic here
        }
        catch (Exception ex)
        {
            m_log.error(ex);
        }
    }

    public static void Salir()
    {
        synchronized (m_estado)
        {
            m_estado = ESTADO_SERVICIO.STOP;
        }
    }

}