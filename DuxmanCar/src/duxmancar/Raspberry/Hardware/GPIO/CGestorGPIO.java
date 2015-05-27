/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Raspberry.Hardware;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.util.List;

/**
 *
 * @author duxman
 */
public class CGestorGPIO
{
    private final static Lock I2CLock = new ReentrantLock(false);    
    private final Logger m_log; 
    private GpioController m_Gpio;
    //private final List<GpioPinDigitalOutput> m_lista;
            
    public CGestorGPIO(  )
    {        
        m_log =  Logger.getRootLogger();
        m_Gpio = GpioFactory.getInstance();
    }
    
    public GpioController dameGPIOController()
    {
        return m_Gpio;
    }
    
    
    public void activarPin()
    {
        
    }
}
