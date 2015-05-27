/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Raspberry.Hardware.ControlMotores;

import duxmancar.Raspberry.Hardware.GPIO.CGpioPines;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.wiringpi.Gpio;
import static java.lang.Math.abs;

/**
 *
 * @author aduce
 */
public class CMotorControlPuenteH
{

  public static int VELOCIDAD_GIRO_1 = 1;
  public static int VELOCIDAD_GIRO_2 = 2;
  public static int VELOCIDAD_GIRO_3 = 3;
  private GpioController m_gpio;
  private CMotorDC m_motorIzq;
  private CMotorDC m_motorDer;

  public CMotorControlPuenteH ()
  {
  }

  public void inicializa (GpioController gpio, boolean bSoftPwm)
  {
    inicializaGPIO (gpio);
    inicializaMotores (bSoftPwm);
  }

  private void inicializaGPIO (GpioController gpio)
  {
    Gpio.wiringPiSetup ();
    m_gpio = gpio;
  }

  private void inicializaMotores (boolean bSoftPwm)
  {
    m_motorDer = new CMotorDC (m_gpio, CGpioPines.GPIO_PIN1_MOTOR_DER, CGpioPines.GPIO_PIN2_MOTOR_DER, CGpioPines.GPIO_PWM_MOTOR_DER, bSoftPwm);
    m_motorIzq = new CMotorDC (m_gpio, CGpioPines.GPIO_PIN1_MOTOR_IZQ, CGpioPines.GPIO_PIN2_MOTOR_IZQ, CGpioPines.GPIO_PWM_MOTOR_IZQ, bSoftPwm);
  }

//  Motor       ENA  IN1  IN2 
//  Forward     High High Low
//  Reverse     High Low  High
//  Coast       Low   X    X     "X" means it can be high or low
//  Brake       High Low  Low    (If both IN1 and IN2 are high, it will also brake)
  public void marchaMotor (boolean bAdelante, int iPotencia)
  {
    m_motorDer.marchaMotor (bAdelante, iPotencia);
    m_motorIzq.marchaMotor (bAdelante, iPotencia);
  }

  public void paroMotor ()
  {
    m_motorDer.paroMotor ();
    m_motorIzq.paroMotor ();
  }

  public void puntoMuertoMotor ()
  {
    m_motorDer.puntoMuerto ();
    m_motorIzq.puntoMuerto ();
  }
  
  public void girarDerecha (int iNivel)
  {
    if (iNivel == VELOCIDAD_GIRO_1)
    {
      m_motorDer.paroMotor ();
      m_motorIzq.marchaMotor (true, 30);
    }
    else if (iNivel == VELOCIDAD_GIRO_2)
    {
      m_motorDer.puntoMuerto ();
      m_motorIzq.marchaMotor (true, 40);
    }
    else if (iNivel == VELOCIDAD_GIRO_3)
    {
      m_motorDer.marchaMotor (false, 45);
      m_motorIzq.marchaMotor (true, 45);
    }
  }

  public void girarIzquierda (int iNivel)
  {
    if (iNivel == VELOCIDAD_GIRO_1)
    {
      m_motorIzq.paroMotor ();
      m_motorDer.marchaMotor (true, 30);
    }
    else if (iNivel == VELOCIDAD_GIRO_2)
    {
      m_motorIzq.puntoMuerto ();
      m_motorDer.marchaMotor (true, 40);
    }
    else if (iNivel == VELOCIDAD_GIRO_3)
    {
      m_motorIzq.marchaMotor (false, 45);
      m_motorDer.marchaMotor (true, 45);
    }        
  }
  
  public void girarRuedas(int iDer, int iIzq)
  {
      boolean delanteDer = true,delanteIzq = true;
      
      if( iDer < 0)      
      {      
          delanteDer = false;
      }
      
      if( iIzq < 0 )
      {       
          delanteIzq = false;
      }      
      
      m_motorDer.marchaMotor(delanteDer, abs(iDer));
      m_motorIzq.marchaMotor(delanteIzq, abs(iIzq));      
  }
  
}
