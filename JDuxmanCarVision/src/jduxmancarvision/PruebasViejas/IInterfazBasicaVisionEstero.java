/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jduxmancarvision.PruebasViejas;

import org.apache.log4j.Logger;
import org.opencv.calib3d.Calib3d;

/**
 *
 * @author duxman
 */
public interface IInterfazBasicaVisionEstero
{
  public static enum eEstadoCalibracion { PARADA,INICIADA,PROCESO,FINALIZADA };
  
  public static int CAMARA1 = 0;
  public static int CAMARA2 = 1;
  
  public static int OK    = 1;
  public static int ERROR = 0;    
  
  public static int  FLAGS_CORNER = Calib3d.CALIB_CB_ADAPTIVE_THRESH | Calib3d.CALIB_CB_FAST_CHECK | Calib3d.CALIB_CB_NORMALIZE_IMAGE;
  public static double TAM_CUADRADO = 50;
}