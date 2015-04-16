/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jduxmancarvision.VisionEstereo;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.opencv.calib3d.Calib3d;
import static org.opencv.calib3d.Calib3d.findChessboardCorners;
import org.opencv.calib3d.StereoBM;
import static org.opencv.calib3d.StereoBM.BASIC_PRESET;
import org.opencv.calib3d.StereoSGBM;
import static org.opencv.core.CvType.CV_16SC2;
import static org.opencv.core.CvType.CV_64F;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.INTER_CUBIC;

/**
 *
 * @author duxman
 */
public class CVisionEstero implements IInterfazBasicaVisionEstero
{    
     Size m_imageSize;
     Size m_parnertSize;
     
     List<Mat>  m_puntosObjetos;
     List< List<Mat> > m_puntosImagenes;
     List<Mat> m_listaImagenes, m_listaR, m_listaT;
     List< MatOfPoint2f > m_listaEsquinas;
     
     Mat m_camaraMatrix,m_camaraDistorsion;
     CCamaraEstereo m_camaras;     
     TermCriteria m_criteria;
     Logger m_log;
     Size m_TamVentana,m_TamZona;
     Mat mx1,my1,mx2,my2;
     
    public CVisionEstero(Size partnerSize)
    {
        m_log = Logger.getRootLogger();
        m_parnertSize = partnerSize;        
        m_camaras = new CCamaraEstereo();
        m_puntosObjetos = new ArrayList<Mat>();
        m_puntosImagenes = new ArrayList< List<Mat> >(2);
        
        m_listaEsquinas = new ArrayList<MatOfPoint2f>();
        
        m_listaR = new ArrayList<Mat>();
        m_listaT = new ArrayList<Mat>();
        m_camaraMatrix = new Mat();
        m_camaraDistorsion = new Mat();
        
        m_criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 40, 0.001);
        m_TamVentana = new Size(5, 5);
        m_TamZona = new Size(-1, -1);         
    }
    

    public void calibrarEstero()
    {
        Mat cameraMatrix1, cameraMatrix2, distCoeffs1, distCoeffs2;
        Mat R, T, E, F;
        
        cameraMatrix1 = Mat.eye(new Size(3,3), CV_64F);
        cameraMatrix2 = Mat.eye(new Size(3,3), CV_64F);
        
        distCoeffs1   = new Mat();
        distCoeffs2   = new Mat();
        
        R = new Mat();
        T = new Mat();
        E = new Mat();
        F = new Mat();
        
        
                //Mat::eye(3, 3, CV_64F);
        //cameraMatrix[1] = Mat::eye(3, 3, CV_64F);
    
        Calib3d.stereoCalibrate( m_puntosObjetos,
              m_puntosImagenes.get(CAMARA1),
              m_puntosImagenes.get(CAMARA2),
              cameraMatrix1,
              cameraMatrix2,
              distCoeffs1,
              distCoeffs2,
              m_imageSize,
              R,T,E,F);
        
        MatOfPoint2f esquinas1 = new MatOfPoint2f( m_puntosImagenes.get(CAMARA1).get(0) );
        MatOfPoint2f esquinas2 = new MatOfPoint2f( m_puntosImagenes.get(CAMARA2).get(0) );
        
        Imgproc.undistortPoints( esquinas1 ,esquinas1, cameraMatrix1,distCoeffs1 );
        Imgproc.undistortPoints( esquinas2 ,esquinas2, cameraMatrix2,distCoeffs2 );
        
        m_puntosImagenes.get(CAMARA1).set(0, esquinas1);
        m_puntosImagenes.get(CAMARA2).set(0, esquinas2);
        
        Mat H1,H2;
        H1 = Mat.eye(new Size(3,3), CV_64F);
        H2 = Mat.eye(new Size(3,3), CV_64F);
        
        Calib3d.stereoRectifyUncalibrated(esquinas1,esquinas2, F, m_imageSize,H1,H2,3);   
        
        Mat R1,R2;
        R1 = Mat.eye(new Size(3,3), CV_64F);
        R2 = Mat.eye(new Size(3,3), CV_64F);
        
        mx1= new Mat();
        mx2= new Mat();
        my1= new Mat();
        my2= new Mat();
        
        Imgproc.initUndistortRectifyMap(cameraMatrix1, distCoeffs1, R1, cameraMatrix1, m_imageSize, CV_16SC2, mx1, my1);
        Imgproc.initUndistortRectifyMap(cameraMatrix2, distCoeffs2, R2, cameraMatrix2, m_imageSize, CV_16SC2, mx2, my2);
    }
    
    public void stereoProcess( Mat img1, Mat img2)
    {
        Mat imgRect1,imgRect2;
        
        imgRect1 = new Mat();
        imgRect2 = new Mat();
        
        Imgproc.remap(img1, imgRect1,mx1, my1,INTER_CUBIC);
        Imgproc.remap(img2, imgRect2,mx2, my2,INTER_CUBIC);
        StereoSGBM BMEstado =  new StereoSGBM();
        
        BMEstado.set_preFilterCap(31);
        BMEstado.set_SADWindowSize(41);
        BMEstado.set_minDisparity(-64);
        BMEstado.set_numberOfDisparities(128);
        BMEstado.set_uniquenessRatio(15);
        
        StereoBM BM = new StereoBM( BASIC_PRESET,16,41);
        
        
    }
    
    public void calibrarCamara()
    {
       Calib3d.calibrateCamera(m_puntosObjetos,m_puntosImagenes.get(0),m_imageSize,m_camaraMatrix,m_camaraDistorsion,m_listaR,m_listaT);
    }
    
    public void calibrarImagenes()
    {
      MatOfPoint3f esquina3Puntos1 = dameEsquina3Puntos();
     
      while(true)
      {        
               
        MatOfPoint2f esquinas1 = new MatOfPoint2f();
        MatOfPoint2f esquinas2 = new MatOfPoint2f();
                              
        boolean bdetectadasEsquinas1 = dameEsquinas( m_camaras.dameImagenGris(CAMARA1), esquinas1 );
        boolean bdetectadasEsquinas2 = dameEsquinas( m_camaras.dameImagenGris(CAMARA2), esquinas2 );
        
        
        if( bdetectadasEsquinas1  && bdetectadasEsquinas2 )
        {                                
            m_puntosObjetos.add(esquina3Puntos1);            
            m_puntosImagenes.get(0).add(esquinas1);            
            m_puntosImagenes.get(1).add(esquinas2);            
            m_listaImagenes.add(m_camaras.dameImagen(CAMARA1));
            
            calibrarEstero();
        }          
      }                
    }
    
    boolean dameEsquinas(Mat gray, MatOfPoint2f corners)
    {
        if (!Calib3d.findChessboardCorners(gray, m_parnertSize, corners, FLAGS_CORNER))
        {
            return false;
        }
        
        Imgproc.cornerSubPix(gray, corners, m_TamVentana, m_TamZona, m_criteria);
        
        return true;
    }

    
    MatOfPoint3f dameEsquina3Puntos()
    {
        MatOfPoint3f corners3f = new MatOfPoint3f();
               
        
        Point3[] vp = new Point3[(int) (m_parnertSize.height  * m_parnertSize.width)];
               
        for ( int i = 0; i < m_parnertSize.height; i++ )
        {
            for ( int j = 0; j < m_parnertSize.width; j++ )
            {
                vp[j] = new Point3(j * TAM_CUADRADO, i * TAM_CUADRADO, 0.0d);
            }
        }
        
        corners3f.fromArray(vp);
        return corners3f;
    }

      
    
}
