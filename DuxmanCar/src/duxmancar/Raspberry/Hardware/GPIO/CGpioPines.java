/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Raspberry.Hardware;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 *
 * @author aduce
 */
public class CGpioPines
{

  public static Pin GPIO_PIN1_MOTOR_DER = RaspiPin.GPIO_15;//pin 8 fisico
  public static Pin GPIO_PIN2_MOTOR_DER = RaspiPin.GPIO_16;//pin 10 fisico
  public static Pin GPIO_PWM_MOTOR_DER = RaspiPin.GPIO_01;//pin 12 fisico
  
  public static Pin GPIO_PIN1_MOTOR_IZQ = RaspiPin.GPIO_22; //pin 31 fisico
  public static Pin GPIO_PIN2_MOTOR_IZQ = RaspiPin.GPIO_23; //pin 33 fisico
  public static Pin GPIO_PWM_MOTOR_IZQ = RaspiPin.GPIO_24; //pin 35 fisico
  
  public static Pin GPIO_PIN_TRIGGER = RaspiPin.GPIO_02; //pin 16 fisico 
  public static Pin GPIO_PIN_HECHO = RaspiPin.GPIO_00; //pin 18 fisico
}
