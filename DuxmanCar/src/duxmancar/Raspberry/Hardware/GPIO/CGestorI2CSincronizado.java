/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Raspberry.Hardware.GPIO;

import com.pi4j.io.i2c.I2CDevice;
import java.util.concurrent.locks.Lock;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public class CGestorI2CSincronizado implements I2CDevice
{

    private final static Lock I2CLock = new ReentrantLock(false);
    private final I2CDevice m_dispositivoI2C;
    private final Logger m_log;

    public CGestorI2CSincronizado(I2CDevice dispositivoI2C)
    {
        m_dispositivoI2C = dispositivoI2C;
        m_log = Logger.getRootLogger();
    }

    @Override public void write(byte b) throws IOException
    {
        m_log.info("Escribimos " + b);
        I2CLock.lock();
        try
        {
            m_dispositivoI2C.write(b);
        }
        finally
        {
            I2CLock.unlock();
        }
    }

    @Override public void write(byte[] buffer, int offset, int size) throws IOException
    {
        m_log.info("Escribimos " + buffer + " en " + offset + " size " + size);
        I2CLock.lock();
        try
        {
            m_dispositivoI2C.write(buffer, offset, size);
        }
        finally
        {
            I2CLock.unlock();
        }
    }

    @Override public void write(int address, byte b) throws IOException
    {
        m_log.info("Escribimos " + b + " en " + address);
        I2CLock.lock();
        try
        {
            m_dispositivoI2C.write(address, b);
        }
        finally
        {
            I2CLock.unlock();
        }
    }

    @Override public void write(int address, byte[] buffer, int offset, int size) throws IOException
    {
        m_log.info("Escribimos " + buffer);
        I2CLock.lock();
        try
        {
            m_dispositivoI2C.write(address, buffer, offset, size);
        }
        finally
        {
            I2CLock.unlock();
        }
    }

    @Override public int read() throws IOException
    {
        I2CLock.lock();
        try
        {
            return m_dispositivoI2C.read();
        }
        finally
        {
            I2CLock.unlock();
        }
    }

    @Override public int read(byte[] buffer, int offset, int size) throws IOException
    {
        I2CLock.lock();
        try
        {
            return m_dispositivoI2C.read(buffer, offset, size);
        }
        finally
        {
            I2CLock.unlock();
        }
    }

    @Override public int read(int address) throws IOException
    {
        I2CLock.lock();
        try
        {
            return m_dispositivoI2C.read(address);
        }
        finally
        {
            I2CLock.unlock();
        }
    }

    @Override public int read(int address, byte[] buffer, int offset, int size) throws IOException
    {
        I2CLock.lock();
        try
        {
            return m_dispositivoI2C.read(address, buffer, offset, size);
        }
        finally
        {
            I2CLock.unlock();
        }
    }

    @Override public int read(byte[] writeBuffer, int writeOffset, int writeSize, byte[] readBuffer, int readOffset, int readSize) throws IOException
    {
        I2CLock.lock();
        try
        {
            return m_dispositivoI2C.read(writeBuffer, writeOffset, writeSize, readBuffer, readOffset, readSize);
        }
        finally
        {
            I2CLock.unlock();
        }
    }

}
