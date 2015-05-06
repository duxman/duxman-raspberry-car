/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jduxmancarvision.OpenCV;

import java.awt.image.BufferedImage;
import static java.lang.Math.tan;
import java.util.ArrayList;
import java.util.List;
import jduxmancarvision.JDuxmanCarVision;
import static jduxmancarvision.OpenCV.Blobs.BLOBLABEL;
import static jduxmancarvision.OpenCV.CDisparidad.DISPARIDAD_UI8;
import org.apache.log4j.Logger;
import org.opencv.calib3d.Calib3d;
import static org.opencv.calib3d.Calib3d.CALIB_ZERO_DISPARITY;
import org.opencv.calib3d.StereoBM;
import org.opencv.calib3d.StereoBM.*;
import org.opencv.calib3d.StereoMatcher;
import org.opencv.calib3d.StereoSGBM;
import org.opencv.core.Core;
import static org.opencv.core.Core.NORM_MINMAX;
import static org.opencv.core.CvType.CV_16S;
import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.core.CvType.CV_8U;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.line;
import static org.opencv.imgproc.Imgproc.putText;
import static org.opencv.imgproc.Imgproc.rectangle;

/**
 *
 * @author duxman
 */
public class CCamaraStereo extends CFuncionesBasicasCamara implements IBasicoVideo
{

    private CCamara m_camaraDerecha;
    private CCamara m_camaraIzquierda;      
    private Mat mx1, my1, mx2, my2, R1, R2, P1, P2;;
    private Size m_imagenSize;
    public Mat m_imagenRectificadaDerecha;
    public Mat m_imagenRectificadaIzquierda;
    public Mat m_DisparidadOriginal;
    public Mat m_imagenProfundidadRectificada;
    public Mat m_perspectiva;
    public Mat m_DisparidadProcesada;
    public List<parejaRegiones> m_listaParejas;
    private List<Mat> m_listaPuntosObjetos;
    private CDisparidad m_disparidad;
    public CCamaraStereo(Size partnerSize) throws InterruptedException
    {
        m_log = Logger.getRootLogger();
        m_partnerSize = partnerSize;        
        m_camaraDerecha = new CCamara(JDuxmanCarVision.CAMARA_DERECHA, CAM_WIDTH, CAM_HEIGTH);        
        m_camaraIzquierda = new CCamara(JDuxmanCarVision.CAMARA_IZQUIERDA, CAM_WIDTH, CAM_HEIGTH);;        
        m_imagenSize = new Size(CAM_WIDTH, CAM_HEIGTH);
        m_listaPuntosObjetos = new ArrayList<Mat>();
        m_disparidad = new CDisparidad();
    }
    
    public int iniciaCamaras() throws InterruptedException
    {
        int rtn, rtn2;
        rtn = m_camaraDerecha.iniciaCamara(JDuxmanCarVision.CAMARA_DERECHA);        
        
        Thread.sleep(2000);
        rtn2 = m_camaraIzquierda.iniciaCamara(JDuxmanCarVision.CAMARA_IZQUIERDA);
        Thread.sleep(2000);
        if (rtn == eValoresRetorno.OK.ordinal()
                && rtn2 == eValoresRetorno.OK.ordinal())
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
    
    public CCamara camaraIzquierda()
    {
        return m_camaraDerecha;
    }
    
    public CCamara camaraDerecha()
    {
        return m_camaraIzquierda;
    }
    
    public void capturarCamaraEstereo()
    {
        m_camaraDerecha.capturarImagen();
        m_camaraIzquierda.capturarImagen();
    }
    
    public Mat[] capturarCamaraEstereoVideo()
    {
        Mat[] ret = {m_camaraDerecha.capturarImagenVideo(), m_camaraIzquierda.capturarImagenVideo()};
        return ret;
    }
    
    public void addPuntos(Size partnerSize,int numpuntos)
    {
       m_partnerSize = partnerSize; 
       for(int i=0;i< numpuntos;i++)
            m_listaPuntosObjetos.add(dameListaPuntos3D());
    }
    
    public boolean calibrarIndividual(Size partnerSize)
    {
        m_partnerSize = partnerSize;
        capturarCamaraEstereo();
        boolean ret = m_camaraDerecha.BuscarAjedrez( m_partnerSize );
        ret = (ret && m_camaraIzquierda.BuscarAjedrez( m_partnerSize ) );
        if( ret == false )
        {
            m_log.warn("Calibracion individual no efectuada");
        }
        else
        {
            m_camaraDerecha.calibrar(partnerSize,false);
            m_camaraIzquierda.calibrar(partnerSize, false );
            m_listaPuntosObjetos.add(dameListaPuntos3D());
        }
        return ret;
    }
    
    public void calibrar()
    {
        
        Mat R, T, E, F;
        
        R =new Mat();// Mat.eye(3,3, CV_64F);
        T =new Mat();// Mat.eye(3,1, CV_64F);
        E =new Mat();// Mat.eye(3,3, CV_64F);
        F =new Mat();// Mat.eye(3,3, CV_64F);
    
        //if (ret == true)
        {
                                  
            TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER,100 ,1e-5 );            
            /*
            Calib3d.stereoCalibrate(m_listaPuntosObjetos,
                    m_camaraDerecha.m_puntosImagen,
                    m_camaraIzquierda.m_puntosImagen,
                    m_camaraDerecha.m_matrizCamara,
                    m_camaraDerecha.m_distorsion,
                    m_camaraIzquierda.m_matrizCamara,
                    m_camaraIzquierda.m_distorsion,
                    m_imagenSize,
                    R, T, E, F,CV_8U)
                    TermCriteria.EPS + TermCriteria.MAX_ITER,
                    Calib3d.CALIB_FIX_ASPECT_RATIO +
                    Calib3d.CALIB_ZERO_TANGENT_DIST +
                    Calib3d.CALIB_SAME_FOCAL_LENGTH );
            */
            R1 = new Mat();//Mat.eye(new Size(3, 3), CV_64F);
            R2 = new Mat();//Mat.eye(new Size(3, 3), CV_64F);            
            P1 = new Mat();//Mat.eye(new Size(3, 4), CV_64F);
            P2 = new Mat();//Mat.eye(new Size(3, 4), CV_64F);
            m_perspectiva = new Mat();//Mat.eye(new Size(4, 4), CV_64F);
            
            mx1 = new Mat();//CAM_WIDTH, CAM_HEIGTH, CV_8U);
            mx2 = new Mat();//CAM_WIDTH, CAM_HEIGTH, CV_8U);
            my1 = new Mat();//CAM_WIDTH, CAM_HEIGTH, CV_8U);
            my2 = new Mat();//CAM_WIDTH, CAM_HEIGTH, CV_8U);
            
            Mat lineas1 = new Mat();
            Mat lineas2 = new Mat();
            
            Rect Roi1, Roi2;
            Roi1 = new Rect();
            Roi2 = new Rect();
                         
           /*
            //m_camara1.m_matrizCamara = Calib3d.getOptimalNewCameraMatrix(m_camara1.m_matrizCamara,m_camara1.m_distorsion,m_imagenSize,1);
            //m_camara2.m_matrizCamara = Calib3d.getOptimalNewCameraMatrix(m_camara2.m_matrizCamara,m_camara2.m_distorsion,m_imagenSize,1);
           */ 
            Imgproc.undistortPoints(m_camaraDerecha.m_esquinas, m_camaraDerecha.m_esquinas, m_camaraDerecha.m_matrizCamara, m_camaraDerecha.m_distorsion);
            Imgproc.undistortPoints(m_camaraIzquierda.m_esquinas, m_camaraIzquierda.m_esquinas, m_camaraIzquierda.m_matrizCamara, m_camaraIzquierda.m_distorsion);
            
            Calib3d.computeCorrespondEpilines(m_camaraDerecha.m_esquinas,1,F,lineas1);
            Calib3d.computeCorrespondEpilines(m_camaraIzquierda.m_esquinas,2,F,lineas2);
            
              Calib3d.stereoRectify(m_camaraDerecha.m_matrizCamara,m_camaraDerecha.m_distorsion,
                                  m_camaraIzquierda.m_matrizCamara,m_camaraIzquierda.m_distorsion,
                                  m_imagenSize,
                                  R,T,
                                  R1,R2,
                                  P1,P2,
                                  m_perspectiva,
                                  CALIB_ZERO_DISPARITY,0,m_imagenSize,
                                  Roi1, Roi2);
            
            
            Imgproc.initUndistortRectifyMap(m_camaraDerecha.m_matrizCamara, m_camaraDerecha.m_distorsion, R1, P1, m_imagenSize, CV_32FC1, mx1, my1);
            Imgproc.initUndistortRectifyMap(m_camaraIzquierda.m_matrizCamara, m_camaraIzquierda.m_distorsion, R2, P2, m_imagenSize, CV_32FC1, mx2, my2);
            
            
            
               
          
            //Imgproc.initUndistortRectifyMap(m_camara1.m_matrizCamara, m_camara1.m_distorsion, new Mat(), new Mat(), m_imagenSize, CV_16SC2, mx1, my1);
            //Imgproc.initUndistortRectifyMap(m_camara1.m_matrizCamara, m_camara1.m_distorsion, new Mat(), new Mat(), m_imagenSize, CV_16SC2, mx2, my2);
            
            /*
            Imgproc.undistortPoints(m_camara1.m_esquinas, m_camara1.m_esquinas, m_camara1.m_matrizCamara, m_camara1.m_distorsion);
            Imgproc.undistortPoints(m_camara2.m_esquinas, m_camara2.m_esquinas, m_camara2.m_matrizCamara, m_camara2.m_distorsion);
            
            
            Calib3d.computeCorrespondEpilines(m_camara1.m_esquinas,1,F,lineas1);
            Calib3d.computeCorrespondEpilines(m_camara2.m_esquinas,2,F,lineas2);
                    
            Mat H1, H2;
            H1 = Mat.eye(new Size(3, 3), CV_64F);
            H2 = Mat.eye(new Size(3, 3), CV_64F);
                        
            F = Calib3d.findFundamentalMat(m_camara1.m_esquinas , m_camara2.m_esquinas, Calib3d.FM_8POINT,0,0);
            
            Calib3d.stereoRectifyUncalibrated(m_camara1.m_esquinas, m_camara2.m_esquinas, F, m_imagenSize, H1, H2, 3);            

           // m_camara1.m_matrizCamara = Calib3d.getOptimalNewCameraMatrix(m_camara1.m_matrizCamara,m_camara1.m_distorsion,m_imagenSize,0);
           // m_camara2.m_matrizCamara = Calib3d.getOptimalNewCameraMatrix(m_camara2.m_matrizCamara,m_camara2.m_distorsion,m_imagenSize,0);
            
            Core.multiply(m_camara1.m_matrizCamara.inv(), H1, R1);
            Core.multiply(R1,m_camara1.m_matrizCamara, R1);
            
            Core.multiply(m_camara2.m_matrizCamara.inv(), H2, R2);
            Core.multiply(R2,m_camara2.m_matrizCamara, R2);
            
            m_camara1.m_matrizCamara.copyTo(P1);
            m_camara2.m_matrizCamara.copyTo(P2);
            
            
            Imgproc.initUndistortRectifyMap(m_camara1.m_matrizCamara, m_camara1.m_distorsion, R1, P1, m_imagenSize, CV_16SC2, mx1, my1);
            Imgproc.initUndistortRectifyMap(m_camara2.m_matrizCamara, m_camara2.m_distorsion, R2, P2, m_imagenSize, CV_16SC2, mx2, my2);
            
            */
            
            
            
            grabarCalibracion();
        }
       
    }
           
      
    public void grabarCalibracion()
    {
        try
        {
            
            TaFileStorage file = new TaFileStorage();
            file.open("DuxmanCalibracion.cal", TaFileStorage.WRITE);
            file.writeMat("M1", m_camaraDerecha.m_matrizCamara);
            file.writeMat("D1", m_camaraDerecha.m_distorsion);
            file.writeMat("R1", R1);
            file.writeMat("P1", P1);
            file.writeMat("mx1", mx1);
            file.writeMat("my1", my1);
            
            file.writeMat("M2", m_camaraIzquierda.m_matrizCamara);
            file.writeMat("D2", m_camaraIzquierda.m_distorsion);
            file.writeMat("R2", R2);
            file.writeMat("P2", P2);
            file.writeMat("mx2", mx2);
            file.writeMat("my2", my2);
            
            file.writeMat("Q", m_perspectiva);
                                    
            file.release();
            
        }
        catch (Exception e)
        {
            m_log.error(e.getMessage());
        }
        
    }
    
    public void cargarCalibracion()
    {
        try
        {
            TaFileStorage file = new TaFileStorage();                    
            
            R1 = new Mat();//Mat.eye(new Size(3, 3), CV_64F);
            R2 = new Mat();//Mat.eye(new Size(3, 3), CV_64F);            
            P1 = new Mat();//Mat.eye(new Size(3, 4), CV_64F);
            P2 = new Mat();//Mat.eye(new Size(3, 4), CV_64F);
            m_perspectiva = new Mat();//Mat.eye(new Size(4, 4), CV_64F);
            
            mx1 = new Mat();//CAM_WIDTH, CAM_HEIGTH, CV_8U);
            mx2 = new Mat();//CAM_WIDTH, CAM_HEIGTH, CV_8U);
            my1 = new Mat();//CAM_WIDTH, CAM_HEIGTH, CV_8U);
            my2 = new Mat();//CAM_WIDTH, CAM_HEIGTH, CV_8U);
            
            file.open("calib/M1.xml", TaFileStorage.READ);                        
            m_camaraDerecha.m_matrizCamara = file.readMat("M1");
            file.open("calib/D1.xml", TaFileStorage.READ);
            m_camaraDerecha.m_distorsion = file.readMat("D1");
            file.open("calib/R1.xml", TaFileStorage.READ);            
            R1 = file.readMat("R1");
            file.open("calib/P1.xml", TaFileStorage.READ);
            P1 = file.readMat("P1");
            file.open("calib/mx1.xml", TaFileStorage.READ);
            mx1 = file.readMat("mx1");
            file.open("calib/my1.xml", TaFileStorage.READ);
            my1 = file.readMat("my1");            
            
            file.open("calib/M2.xml", TaFileStorage.READ);
            m_camaraIzquierda.m_matrizCamara = file.readMat("M2");
            file.open("calib/D2.xml", TaFileStorage.READ);
            m_camaraIzquierda.m_distorsion = file.readMat("D2");
            file.open("calib/R2.xml", TaFileStorage.READ);
            R2 = file.readMat("R2");
            file.open("calib/P2.xml", TaFileStorage.READ);
            P2 = file.readMat("P2");
            file.open("calib/mx2.xml", TaFileStorage.READ);
            mx2 = file.readMat("mx2");
            file.open("calib/my2.xml", TaFileStorage.READ);
            my2 = file.readMat("my2");                        
            file.open("calib/Q.xml", TaFileStorage.READ);
            m_perspectiva = file.readMat("Q");                       
        }
        catch (Exception e)
        {
            m_log.error(e.getMessage());
        }
        
    }
    
      public Mat procesarImagen3d(int mindisp ,int maxdisp, int maxperpixel,int validate, double texture )
    {
        capturarCamaraEstereo();
        
                
        m_imagenRectificadaDerecha = new Mat();//CAM_WIDTH, CAM_HEIGTH, CV_8U);
        m_imagenRectificadaIzquierda = new Mat();
        
        Imgproc.remap(m_camaraDerecha.dameImagen(), m_imagenRectificadaDerecha, mx1, my1, Imgproc.INTER_LINEAR);
        Imgproc.remap(m_camaraIzquierda.dameImagen(), m_imagenRectificadaIzquierda, mx2, my2, Imgproc.INTER_LINEAR);
        
        
        BufferedImage bufImgIzq = m_disparidad.mat2Img(m_imagenRectificadaIzquierda);
        BufferedImage bufImgDer = m_disparidad.mat2Img(m_imagenRectificadaDerecha);
        m_disparidad.denseDisparitySubpixel(bufImgIzq, bufImgDer, 5, mindisp, maxdisp, maxperpixel,validate,texture);
        //m_disparidad.showDisparity(CDisparidad.DISPARIDAD_UI8, mindisp+10, maxdisp-100 );
        line(m_imagenRectificadaIzquierda,new Point(160,1), new Point(160,239),new Scalar(255,0,0),3,8,0);
        line(m_imagenRectificadaIzquierda,new Point(1,120), new Point(359,120),new Scalar(255,0,0),3,8,0);
                
        return m_disparidad.getMatDisparity(CDisparidad.DISPARIDAD_F32, mindisp, maxdisp);                
    }
      
    public Mat procesarImagen3d(int imindisp ,int imaxdisp ,int preFilterSize ,int preFilterCap ,int SADWindowSize,int textureThreshold,int uniquenessRatio  )
    {
        
     
        capturarCamaraEstereo();
        Mat imagenIzquierda = m_camaraIzquierda.dameImagenGris();
        Mat imagenDerecha = m_camaraDerecha.dameImagenGris();        
        return procesarImagen(imagenIzquierda, imagenDerecha, imindisp ,imaxdisp ,preFilterSize ,preFilterCap ,SADWindowSize,textureThreshold,uniquenessRatio               );
    }
    
    private Mat procesarImagen(Mat imgIzq, Mat imgDer,int imindisp ,int imaxdisp ,int preFilterSize ,int preFilterCap ,int SADWindowSize,int textureThreshold,int uniquenessRatio  )
    {        
        
        m_imagenRectificadaDerecha = new Mat();//CAM_WIDTH, CAM_HEIGTH, CV_8U);
        m_imagenRectificadaIzquierda = new Mat();//CAM_WIDTH, CAM_HEIGTH, CV_8U);CAM_WIDTH, CAM_HEIGTH, CV_8U);
        m_DisparidadOriginal = new Mat();//CAM_WIDTH, CAM_HEIGTH, CV_16S);
        m_DisparidadProcesada = new Mat();//CAM_WIDTH, CAM_HEIGTH, CV_32F);
        m_imagenProfundidadRectificada = new Mat();//CAM_WIDTH, CAM_HEIGTH, CV_32F);
        
        Imgproc.remap(imgIzq, m_imagenRectificadaIzquierda , mx1, my1, Imgproc.INTER_LINEAR);
        Imgproc.remap(imgDer, m_imagenRectificadaDerecha, mx2, my2, Imgproc.INTER_LINEAR);
        
      
       //StereoBM steroBM = new StereoBM(BASIC_PRESET, disp, sombras);
       //steroBM.compute(m_imagenRectificadaIzquierda, m_imagenRectificadaDerecha, m_DisparidadOriginal,CV_32F);
        StereoBM sb = StereoBM.create(imaxdisp,SADWindowSize);
        /*sb.setPreFilterSize(preFilterSize);
        sb.setPreFilterCap(preFilterCap);
        sb.setBlockSize(SADWindowSize);
        sb.setMinDisparity(imindisp);
        sb.setNumDisparities(imaxdisp);
        sb.setTextureThreshold(textureThreshold);
        sb.setUniquenessRatio(uniquenessRatio);
        */
        sb.compute(m_imagenRectificadaIzquierda, m_imagenRectificadaDerecha, m_DisparidadOriginal);        
        imshow
       /* StereoSGBM sb = StereoSGBM.create(imindisp, imaxdisp, SADWindowSize);
        sb.setMode(CV_32F);       
        sb.setPreFilterCap(preFilterCap);
        sb.setBlockSize(SADWindowSize);
        sb.setMinDisparity(imindisp);
        sb.setNumDisparities(imaxdisp);        
        sb.setUniquenessRatio(uniquenessRatio);
        sb.compute(m_imagenRectificadaIzquierda, m_imagenRectificadaDerecha, m_DisparidadOriginal);        
        */
        
     /*   int P1 = 8*sombras*sombras*m_imagenRectificadaIzquierda.channels();
        int P2 = 32*sombras*sombras*m_imagenRectificadaIzquierda.channels();;
        StereoSGBM steroBM = new StereoSGBM(mindisp,disp,sombras,P1,P2,imaxdiff,iPrefilterCap,iuniqueratio,ispecksize,ispeckrange,false);
        steroBM.compute(m_imagenRectificadaIzquierda, m_imagenRectificadaDerecha, m_DisparidadOriginal);
        */
       
        
        
        m_DisparidadOriginal.convertTo(m_DisparidadProcesada,CV_32F , 1/16);//255/(minmaxResult.maxVal - minmaxResult.minVal));        
        Core.normalize(m_DisparidadOriginal, m_DisparidadProcesada,0,256,Core.NORM_MINMAX);
       
        
         Calib3d.reprojectImageTo3D(m_DisparidadOriginal,m_imagenProfundidadRectificada,m_perspectiva,true,CV_32F);
        
        //,0,32,NORM_MINMAX);        
        return m_imagenProfundidadRectificada;
    }
    
    public Mat dameImagenProcesada()
    {
        return m_DisparidadProcesada;
    }
    
    public void dameBlobsimagenProcesada()
    {
        
    }
    
    public double dameDistanciaAZona1()
    {                        
        double dDisparidad= m_disparidad.getdisparityPixel(160, 120);
         circle(m_imagenRectificadaDerecha, new Point(dDisparidad,120),3, new Scalar(255,0,0), 2);
         circle(m_imagenRectificadaDerecha, new Point(dDisparidad-160,120),3, new Scalar(255,255,0), 2);
         circle(m_imagenRectificadaDerecha, new Point(dDisparidad-50,120),3, new Scalar(0,255,0), 2);
        //double dDistanciaFocalPixel = (m_imagenSize.width * 0.5)/tan((FOCAL_FOV *0.5 *FOCAL_PI)/180 );
                               
        double distance_mm = LENS_DISTANCE * 316.63 /( dDisparidad);   
        
        return distance_mm;
    }
    public double dameDistanciaAZona()
    {        
        short[] aDisparidad = new short[1];
        float idisparidad = aDisparidad[0];//0xffff;
        double[][] valores ;
        m_DisparidadOriginal.get(240, 320, aDisparidad);
        
        double Z = 7666;
        //if( idisparidad > 0)
        {                              
           double dDistanciaFocalPixel = 260.089;          
           double W = idisparidad;           
           Z = Z / aDisparidad[0];
                             
           circle(m_imagenRectificadaDerecha, new Point(idisparidad,240),3, new Scalar(255,0,0), 2);
           circle(m_imagenRectificadaDerecha, new Point(320 - idisparidad ,240),4, new Scalar(255,255,0), 2);
           circle(m_imagenRectificadaDerecha, new Point(320 + idisparidad ,240),5, new Scalar(0,255,0), 2);
           
            //m_disparidad.showDisparity(CDisparidad.DISPARIDAD_UI8, mindisp+10, maxdisp-100 );
           line(m_imagenRectificadaIzquierda,new Point(320,1), new Point(320,480),new Scalar(255,0,0),3,8,0);
           line(m_imagenRectificadaIzquierda,new Point(1,240), new Point(639,240),new Scalar(255,0,0),3,8,0);
           m_log.info("Z:" + Z + " Disparidad :"  + aDisparidad[0]);
           return Z;
           
        }
       // return 0;
        
    }
    public void detectarObjetos(int iThresholdmin, int ithresholdMax, int iThresholdType, int iPunto, int iAreaMin)
    {
        capturarCamaraEstereo();
        m_camaraDerecha.detectarObjetos(iThresholdmin, ithresholdMax, iThresholdType, iPunto, iAreaMin);
        m_camaraIzquierda.detectarObjetos(iThresholdmin, ithresholdMax, iThresholdType, iPunto, iAreaMin);        
        
        detectarRegionesSimilares();
    }
    
    private void detectarRegionesSimilares()
    {
        int imaxRegions1 = m_camaraDerecha.m_Regions.MaxLabel;
        int imaxRegions2 = m_camaraDerecha.m_Regions.MaxLabel;
        m_listaParejas = new ArrayList<>();
        
        for(int i = 0 ;  i< imaxRegions1;i++ )
        {
            for(int j = 0 ;  j< imaxRegions2;j++ )
            {
               double [] Region1=m_camaraDerecha.m_Regions.RegionData[i];
               double [] Region2=m_camaraIzquierda.m_Regions.RegionData[j];
               
               if( Region1[BLOBLABEL] != 0.0  && Region2[BLOBLABEL]!=0.0  )
               {
                    int MaxX1 = (int) Region1[Blobs.BLOBMAXX];
                    int MaxY1 = (int) Region1[Blobs.BLOBMAXY]; 
                    int MinX1 = (int) Region1[Blobs.BLOBMINX];            
                    int MinY1 = (int) Region1[Blobs.BLOBMINY];
                    int area1 = (int) Region1[Blobs.BLOBAREA];     
                    
                    int MaxX2 = (int) Region2[Blobs.BLOBMAXX];
                    int MaxY2 = (int) Region2[Blobs.BLOBMAXY]; 
                    int area2 = (int) Region2[Blobs.BLOBAREA];     
                    
                    if ( similitud ( Region1[Blobs.BLOBAREA], Region2[Blobs.BLOBAREA], 0.9 ) )                  
                    {
                        m_listaParejas.add( new parejaRegiones(i, j, Region1, Region2));
                        rectangle(m_camaraDerecha.m_ultimaImagen, new Point(MinX1, MinY1), new Point(MaxX1,MaxY1), new Scalar(0, 255, 0),3);
                        String text= "("+i+","+j+")["+area1+"-"+area2+"]";
                        putText(m_camaraDerecha.m_ultimaImagen, text,new Point(MaxX1/2,MaxY1/2),3,-2, new Scalar(255, 0, 0));
                        putText(m_camaraIzquierda.m_ultimaImagen, text,new Point(MaxX2/2,MaxY2/2),3,-2, new Scalar(255, 0, 0));
                    }               
               }
            }            
        }
    }
    
    private boolean similitud( double valor1, double valor2, double percent )
    {
        boolean rtn= false;
        if ( ( ( ( valor1 * 0.9 ) <= ( valor2) ) &&
               ( ( valor2 * 0.9 ) <= ( valor1) ) ) ||
               ( ( valor2   ) == ( valor1) ) )
        {
            rtn =true;
        }
        return rtn;
                
    }
    
    class parejaRegiones
    {
       public int m_iRegion1;
       public int m_iRegion2;
       public double[] m_region1;
       public double[] m_region2;
      
       public parejaRegiones( int ir1, int ir2, double[] r1, double[] r2)
       {
           m_iRegion1 = ir1;
           m_iRegion2 = ir2;
           m_region1 = r1;
           m_region2 = r2;
       }
               
    }
    
    
}
