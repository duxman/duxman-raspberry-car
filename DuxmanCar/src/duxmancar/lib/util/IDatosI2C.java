/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.lib.util;

/**
 *
 * @author duxman
 */
public interface IDatosI2C
{
  public final static int SUBADR1       = 0x02;
  public final static int SUBADR2       = 0x03;
  public final static int SUBADR3       = 0x04;
  public final static int MODE1         = 0x00;
  public final static int PRESCALE      = 0xFE;
  public final static int LED0_ON_L     = 0x06;
  public final static int LED0_ON_H     = 0x07;
  public final static int LED0_OFF_L    = 0x08;
  public final static int LED0_OFF_H    = 0x09;
  public final static int ALL_LED_ON_L  = 0xFA;
  public final static int ALL_LED_ON_H  = 0xFB;
  public final static int ALL_LED_OFF_L = 0xFC;
  public final static int ALL_LED_OFF_H = 0xFD;
    
}
