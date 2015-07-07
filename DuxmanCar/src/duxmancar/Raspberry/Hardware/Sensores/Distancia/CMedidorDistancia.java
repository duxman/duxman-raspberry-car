/* 
 * 2014 Jose Cruz <joseacruzp@gmail.com>.
 */
package duxmancar.Raspberry.Hardware.Sensores.Distancia;

import static duxmancar.Raspberry.Hardware.GPIO.CGpioPines.GPIO_PIN_HECHO;
import static duxmancar.Raspberry.Hardware.GPIO.CGpioPines.GPIO_PIN_TRIGGER;
import duxmancar.Raspberry.Hardware.Sensores.CSensor;
import java.io.IOException;
import org.apache.log4j.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;

/**
 * Interface to Ultrasound HC-SR04 device
 *
 * @author Jose Cruz
 */
public class CMedidorDistancia extends CSensor
{

    private final int PULSE = 10000;        // #10us pulse 10.000 ns
    private final int SPEEDOFSOUND = 34029; // Speed Sound 34029 cm/s

    private GPIOPin trigger = null;
    private GPIOPin echo = null;
    private double m_distanciaMedia;
    private static CMedidorDistancia m_instance = null;
    
    public static CMedidorDistancia getInstance() throws Exception
    {
        if( m_instance == null )
        {
             m_instance = new CMedidorDistancia(GPIO_PIN_TRIGGER, GPIO_PIN_HECHO);
        }
        return m_instance;
    }
    /**
     * Inicialize GPIO to echo and trigger pins
     *
     * @param _trigger
     * @param _echo
     */
    private CMedidorDistancia(int _trigger, int _echo) throws Exception
    {
        m_log = Logger.getRootLogger();
        try
        {
            // define device for trigger pin
            trigger = (GPIOPin) DeviceManager.open(new GPIOPinConfig(
                    0, _trigger, GPIOPinConfig.DIR_OUTPUT_ONLY, GPIOPinConfig.MODE_OUTPUT_PUSH_PULL,
                    GPIOPinConfig.TRIGGER_NONE, false));
            // define device for echo pin
            echo = (GPIOPin) DeviceManager.open(new GPIOPinConfig(
                    0, _echo, GPIOPinConfig.DIR_INPUT_ONLY, GPIOPinConfig.MODE_INPUT_PULL_UP,
                    GPIOPinConfig.TRIGGER_NONE, false));
            Thread.sleep(1000);

        }
        catch (IOException ex)
        {
            m_log.error(ex.getMessage());
        }
    }

    /**
     * Send a pulse to HCSR04 and compute the echo to obtain distance
     *
     * @return distance in cm/s
     */
    public double pulse() throws Exception
    {
        long distance = 0;
        try
        {
            trigger.setValue(true);         //Send a pulse trigger must be 1 and 0 with a 10 us wait
            Thread.sleep(0, PULSE);
            trigger.setValue(false);
            long starttime = System.nanoTime(); //ns
            long stop = starttime;
            long start = starttime;
            //echo will go 0 to 1 and I need save time for that. 2 seconds difference
            while ((!echo.getValue()) && (start < starttime + 250000000L ))
            {
                start = System.nanoTime();
            }
            while ((echo.getValue()) && (stop < starttime + 250000000L ))
            {
                stop = System.nanoTime();
            }
            long delta = (stop - start);
            distance = delta * SPEEDOFSOUND;       // echo from 0 to 1 depending object distance
        }
        catch (IOException ex)
        {
            m_log.error(ex.getMessage());
        }
        return distance / 2.0 / (1000000000L); // cm/s
    }

    /**
     * Free device GPIO
     */
    public void close()
    {
        try
        {
            if ((trigger != null) && (echo != null))
            {
                trigger.close();
                echo.close();;
            }
        }
        catch (IOException ex)
        {
            m_log.error(ex.getMessage());
        }
    }

    @Override
    public void medir()
    {
        double dTotalMediciones=0; 
        double dMedicion=0;
        for(int i =0 ; i< NUMERO_MEDICIONES ; i++)
        {
            try
            {
                dMedicion = pulse();
                dTotalMediciones += dMedicion;
                m_log.info("Distancia pulso " +  i + " es " + dMedicion );
            }
            catch (Exception ex)
            {
               m_log.error(ex.getMessage());
            }
        }

        m_distanciaMedia = dTotalMediciones / NUMERO_MEDICIONES;                
        m_log.info("Distancia media de " +  NUMERO_MEDICIONES + " es " + m_distanciaMedia );
    }

    /**
     * @return the m_distanciaMedia
     */
    public double getDistanciaMedia()
    {
        return m_distanciaMedia;
    }
    
}
