/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Paquetes.Raspberry.Hardware;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import static duxmancar.ServoTest.bus;
import duxmancar.lib.util.IDatosI2C;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public class CGestorI2CAdafruit implements IDatosI2C
{

    private CGestorI2CSincronizado gestorI2C;    
    private int m_frecuenciaI2C;
    private final Logger m_log;
    private I2CBus bus;

    public CGestorI2CAdafruit(int iPuertoI2C, int iFrecuencia) throws IOException
    {               
        m_log = Logger.getRootLogger();
        
        m_log.info("Inicializamos BUS I2C" );
        bus = I2CFactory.getInstance(I2CBus.BUS_1); // Depends onthe RasPI version 
        
        m_log.info("Inicializamos dispositivo");
        
        gestorI2C = new CGestorI2CSincronizado( bus.getDevice(iPuertoI2C) );
        
        m_log.info("Configuramos dispositivo");        
        gestorI2C.write(MODE1, (byte) 0x00);
        
        m_log.info("Asignamos la frecuencia al dispositivo");
        setPWMFreq(iFrecuencia);
    }

    private void waitfor(long howMuch)
    {
        try
        {
            Thread.sleep(howMuch);
        }
        catch (InterruptedException ie)
        {
            m_log.error("Error Parada");
        }
    }

    public void setPWMFreq(int freq)
    {
        m_frecuenciaI2C = freq;
        float preScaleVal = 25000000.0f; // 25MHz
        preScaleVal /= 4096.0;           // 4096: 12-bit
        preScaleVal /= freq;
        preScaleVal -= 1.0;
        double preScale = Math.floor(preScaleVal + 0.5);

        try
        {
            byte oldmode = (byte) gestorI2C.read(MODE1);
            byte newmode = (byte) ((oldmode & 0x7F) | 0x10); // sleep
            gestorI2C.write(MODE1, newmode);              // go to sleep
            gestorI2C.write(PRESCALE, (byte) (Math.floor(preScale)));
            gestorI2C.write(MODE1, oldmode);
            waitfor(5);
            gestorI2C.write(MODE1, (byte) (oldmode | 0x80));
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    /**
     *
     * @param channel 0..15
     * @param on 0..4095 (2^12 positions)
     * @param off 0..4095 (2^12 positions)
     */
    public void setPWM(int channel, int on, int off) throws IllegalArgumentException
    {
        m_log.info("Mandamos datos setPWM " + channel + " on " + on + " off " + off);
        if (channel < 0 || channel > 15)
        {
            throw new IllegalArgumentException("Channel must be in [0, 15]");
        }
        if (on < 0 || on > 4095)
        {
            throw new IllegalArgumentException("On must be in [0, 4095]");
        }
        if (off < 0 || off > 4095)
        {
            throw new IllegalArgumentException("Off must be in [0, 4095]");
        }
        if (on > off)
        {
            throw new IllegalArgumentException("Off must be greater than On");
        }
        try
        {
            gestorI2C.write(LED0_ON_L + 4 * channel, (byte) (on & 0xFF));
            gestorI2C.write(LED0_ON_H + 4 * channel, (byte) (on >> 8));
            gestorI2C.write(LED0_OFF_L + 4 * channel, (byte) (off & 0xFF));
            gestorI2C.write(LED0_OFF_H + 4 * channel, (byte) (off >> 8));
        }
        catch (IOException ioe)
        {
            m_log.error("Error asignando datos I2C ");
        }
    }

    /**
     *
     * @param channel 0..15
     * @param pulseMS in ms.
     */
    public void setServoPulse(int channel, float pulseMS)
    {
        double pulseLength = 1000000; // 1s = 1,000,000 us per pulse. "us" is to be read "micro (mu) sec".
        pulseLength /= m_frecuenciaI2C;  // 40..1000 Hz
        pulseLength /= 4096;       // 12 bits of resolution
        int pulse = (int) (pulseMS * 1000);
        pulse /= pulseLength;       
        this.setPWM(channel, 0, pulse);
    }

}
