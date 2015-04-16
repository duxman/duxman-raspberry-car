/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jduxmancarvision.OpenCV;

import org.opencv.calib3d.Calib3d;

/**
 *
 * @author duxman
 */
public interface IBasicoVideo
{
  public static enum eValoresRetorno  { ERROR, OK, INCIERTO };
  public static enum eEstadoCalibracion { STOP, INCICIADO, PROCESO, FINALIZADO };
  
  public static int CAMARA1 = 0;
  public static int CAMARA2 = 1;
  public static int CAM_WIDTH = 320;
  public static int CAM_HEIGTH = 240;
  
  public static int  FLAGS_CORNER = Calib3d.CALIB_CB_ADAPTIVE_THRESH | Calib3d.CALIB_CB_FAST_CHECK | Calib3d.CALIB_CB_NORMALIZE_IMAGE;
  public static int  FLAGS_CALIB = Calib3d.CALIB_ZERO_TANGENT_DIST | Calib3d.CALIB_FIX_PRINCIPAL_POINT | Calib3d.CALIB_FIX_K4 | Calib3d.CALIB_FIX_K5;
}
