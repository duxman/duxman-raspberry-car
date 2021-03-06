/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jduxmancarvision.OpenCV;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;

/**
 *
 * @author duxman
 */
public interface IBasicoVideo
{
  public static enum eValoresRetorno  { ERROR, OK, INCIERTO };
  public static enum eEstadoCalibracion { STOP, INCICIADO, PROCESO, FINALIZADO };
    
  public static int CAM_WIDTH = 320;
  public static int CAM_HEIGTH = 240;
  public static int TAM_CUADRADO = 30;

  public static double  FOCAL_LENGTH=  2.3;
  public static double  LENS_DISTANCE=  111;
  public static int     FOCAL_FOV = 58;  
  public static double  FOCAL_PI = 3.141596;  
  
  public static int  FLAGS_CORNER = Calib3d.CALIB_CB_ADAPTIVE_THRESH | Calib3d.CALIB_CB_FAST_CHECK | Calib3d.CALIB_CB_NORMALIZE_IMAGE;
  public static int  FLAGS_CALIB = Calib3d.CALIB_ZERO_TANGENT_DIST | Calib3d.CALIB_FIX_PRINCIPAL_POINT | Calib3d.CALIB_FIX_K4 | Calib3d.CALIB_FIX_K5;
  
  public static String COMANDO_VIDEO_DEV_IZQ = "ls -l /dev/video-der";
  public static String COMANDO_VIDEO_DEV_DER = "ls -l /dev/video-izq";
  public static String CADENA_BUSCAR= "-> video";
  
 
}
