/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Raspberry.Hardware.ControlMotores;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.wiringpi.SoftPwm;

/**
 *
 * @author aduce
 */
public class CMotorDC
{

  private GpioPinDigitalOutput m_motorPin1;
  private GpioPinDigitalOutput m_motorPin2;
  private GpioPinDigitalOutput m_motorPwm;
  private int m_iSoftPwmId;
  private boolean m_bSoftPwm;
  private static CMotorDC instance=null;
    
  public  CMotorDC (GpioController gpio, Pin pin1, Pin pin2, Pin Pwm, boolean bSoftPwm)
  {
    m_bSoftPwm = bSoftPwm;
    if (m_bSoftPwm == true)
    {
      com.pi4j.wiringpi.Gpio.wiringPiSetup ();
      m_iSoftPwmId = Pwm.getAddress ();
      m_motorPin1 = gpio.provisionDigitalOutputPin (pin1);
      m_motorPin2 = gpio.provisionDigitalOutputPin (pin2);
      SoftPwm.softPwmCreate (m_iSoftPwmId, 0, 100);
      SoftPwm.softPwmWrite (m_iSoftPwmId, 0);
    }
    else
    {
      m_iSoftPwmId = 0;
      m_motorPin1 = gpio.provisionDigitalOutputPin (pin1);
      m_motorPin2 = gpio.provisionDigitalOutputPin (pin2);
      m_motorPwm = gpio.provisionDigitalOutputPin (Pwm);
      m_motorPwm.setMode (PinMode.PWM_OUTPUT);
      m_motorPwm.low ();
    }
  }

  public void paroMotor ()
  {
    try
    {
        m_motorPin1.low ();
        m_motorPin2.low ();

        if (m_bSoftPwm == false)
        {
          m_motorPwm.high ();
        }
        else
        {
          SoftPwm.softPwmWrite (m_iSoftPwmId, 1);
        }
    }
    catch(Exception e)
    {     
        System.out.print(e.getMessage());
    }
  }

  public void puntoMuerto ()
  {
    try
    {
        m_motorPin1.low ();
        m_motorPin2.low ();
        if (m_bSoftPwm == false)
        {
          m_motorPwm.low ();
        }
        else
        {
          SoftPwm.softPwmWrite (m_iSoftPwmId, 0);
        }
    }
    catch(Exception e)
    {     
        System.out.print(e.getMessage());
    }
  }

  public void marchaMotor (boolean bAdelante, int iPotencia)
  {
    try
    {
      activarMotor (bAdelante);
      velocidadMotor (iPotencia);
    }
    catch(Exception e)
    {     
        System.out.print(e.getMessage());
    }
  }

  private void velocidadMotor (int iPotencia)
  {
    if (m_bSoftPwm == false)
    {
      m_motorPwm.pulse (iPotencia);
    }
    else
    {
      SoftPwm.softPwmWrite (m_iSoftPwmId, iPotencia);
    }
  }

  private void activarMotor (boolean bAdelante)
  {
    if (bAdelante == true)
    {
      m_motorPin1.high ();
      m_motorPin2.low ();
    }
    else
    {
      m_motorPin1.low ();
      m_motorPin2.high ();
    }
  }
}
