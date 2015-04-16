/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jduxmancarvision.OpenCV;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jduxmancarvision.VisionEstereo.IInterfazBasicaVisionEstero.CAMARA1;
import static jduxmancarvision.VisionEstereo.IInterfazBasicaVisionEstero.CAMARA2;
import org.opencv.calib3d.Calib3d;
import org.opencv.calib3d.StereoBM;
import static org.opencv.calib3d.StereoBM.BASIC_PRESET;
import org.opencv.core.Core;
import static org.opencv.core.Core.NORM_MINMAX;
import static org.opencv.core.CvType.CV_16S;
import static org.opencv.core.CvType.CV_16SC2;
import static org.opencv.core.CvType.CV_64F;
import static org.opencv.core.CvType.CV_8U;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author duxman
 */
public class CCamaraStereo implements IBasicoVideo
{
    private CCamara m_camara1;
    private CCamara m_camara2;
    
    private Size m_partnerSize;
    private Mat mx1,my1,mx2,my2;
    private Size m_imagenSize;
    private Mat m_imagenRectificada1;
    private Mat m_imagenRectificada2;
    private Mat m_imagenProfundidad;
    private Mat m_imagenProfundidadRectificada;
    
    public CCamaraStereo( Size partnerSize)
    {
        m_partnerSize = partnerSize;
                
        m_camara1 = new CCamara (CAMARA1, CAM_WIDTH, CAM_HEIGTH);
        m_camara2 = new CCamara (CAMARA2, CAM_WIDTH, CAM_HEIGTH);;    
        
        m_imagenSize = new Size( CAM_WIDTH, CAM_HEIGTH);
    }
  
    public void capturarCamaraEstereo()
    {
        m_camara1.capturarImagen();
        m_camara2.capturarImagen();
    }
    
    public void calibrar()
    {
      Mat R, T, E, F;
      
      R = new Mat();
      T = new Mat();
      E = new Mat();
      F = new Mat();
      m_camara1.calibrar ( m_partnerSize );
      m_camara2.calibrar ( m_partnerSize );
      
      List<Mat> listaPuntosObjetos =  new ArrayList<>(2);
      listaPuntosObjetos.addAll( m_camara1.get_puntosObjeto() );
      listaPuntosObjetos.addAll( m_camara2.get_puntosObjeto() );
      TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 40, 0.001); 
      
      Calib3d.stereoCalibrate( listaPuntosObjetos, 
                               m_camara1.get_puntosImagen(),
                               m_camara2.get_puntosImagen(),
                               m_camara1.get_matrizCamara(),
                               m_camara1.get_distorsion(),
                               m_camara2.get_matrizCamara(),
                               m_camara2.get_distorsion(),
                               m_imagenSize,
                               R,T,E,F );
      
        MatOfPoint2f esquinas1 = m_camara1.get_esquinas();
        MatOfPoint2f esquinas2 = m_camara2.get_esquinas();
        
        Imgproc.undistortPoints( esquinas1 ,esquinas1, m_camara1.get_matrizCamara(), m_camara1.get_distorsion() );
        Imgproc.undistortPoints( esquinas2 ,esquinas2, m_camara2.get_matrizCamara(), m_camara2.get_distorsion() );
        
        m_camara1.set_esquinas(esquinas1);
        m_camara2.set_esquinas(esquinas2);
        
        Mat H1,H2;
        H1 = Mat.eye(new Size(3,3), CV_64F);
        H2 = Mat.eye(new Size(3,3), CV_64F);
        
        Calib3d.stereoRectifyUncalibrated( esquinas1,esquinas2, F, m_imagenSize ,H1,H2,3);   
        
        Mat R1,R2;
        R1 = Mat.eye(new Size(3,3), CV_64F);
        R2 = Mat.eye(new Size(3,3), CV_64F);
        
        mx1= new Mat();
        mx2= new Mat();
        my1= new Mat();
        my2= new Mat();
        
        Imgproc.initUndistortRectifyMap(m_camara1.get_matrizCamara(), m_camara1.get_distorsion(), R1, m_camara1.m_matrizCamara, m_imagenSize , CV_16SC2, mx1, my1);
        Imgproc.initUndistortRectifyMap(m_camara2.get_matrizCamara(), m_camara2.get_distorsion(), R2, m_camara2.m_matrizCamara, m_imagenSize , CV_16SC2, mx2, my2);
        
        grabarCalibracion();
    }

    private MatOfPoint3f dameListaPuntos3D()
    {
        MatOfPoint3f corners3f = new MatOfPoint3f();                
        int cnt =0;
        Point3[] vp = new Point3[ (int) (m_partnerSize.height * m_partnerSize.width) * 2 ];                
        for(int k=0;k<2;k++)
        {
            for(int i = 0; i < m_partnerSize.height; i++ )
            {
                for(int j = 0; j < m_partnerSize.width; j++ )
                {                    
                    vp[cnt] = new Point3(i, j, 0);
                    cnt++;
                }
            }
        }
        
        corners3f.fromArray(vp);
        return corners3f;
    }
    
    private void writeFloatArray( FileChannel out, Mat matriz ) throws IOException
    {
        float[] data = new float[  (int)( matriz.total() * matriz.channels() ) ];
        matriz.get(0, 0, data);
        
        ByteBuffer buf = ByteBuffer.allocate( (int)(4*matriz.total()) );
        buf.clear();
        buf.asFloatBuffer().put(data);
        out.write(buf);        
    }
    
    private void readFloatArray( FileChannel input, Mat matriz ) throws IOException
    {   
        float[] data = new float[  CAM_WIDTH*CAM_HEIGTH ];
        ByteBuffer buf_in = ByteBuffer.allocate(CAM_WIDTH*CAM_HEIGTH*4);        
        buf_in.clear();
        input.read(buf_in);
        buf_in.rewind();
        buf_in.asFloatBuffer().get(data);
        matriz = new Mat();
        matriz.put(0, 0, data);
    }
    
    public void grabarCalibracion()
    {
        try
        {
            RandomAccessFile aFile = new RandomAccessFile("DuxmanCalibracion.cal", "w");
            FileChannel outChannel = aFile.getChannel();
            writeFloatArray( outChannel , mx1);
            writeFloatArray( outChannel , my1);
            writeFloatArray( outChannel , mx2);
            writeFloatArray( outChannel , my2);                            
            outChannel.close();
        }
        catch ( Exception e )
        {
           
        }
        
    }

    public void cargarCalibracion()
    {
         try
        {
            RandomAccessFile aFile = new RandomAccessFile("DuxmanCalibracion.cal", "r");
            FileChannel inputChannel = aFile.getChannel();            
            readFloatArray( inputChannel , mx1);
            readFloatArray( inputChannel , my1);
            readFloatArray( inputChannel , mx2);
            readFloatArray( inputChannel , my2);  
            inputChannel.close();
        }
        catch ( Exception e )
        {
           
        }

    }

    public void procesarImagen( Mat imgIzq, Mat imgDer )
    {                       
        m_imagenRectificada1 =  new Mat(CAM_HEIGTH,CAM_WIDTH,CV_8U);
        m_imagenRectificada2 =new Mat(CAM_HEIGTH,CAM_WIDTH,CV_8U);;
        m_imagenProfundidad = new Mat(CAM_HEIGTH,CAM_WIDTH,CV_16S);;
        m_imagenProfundidadRectificada = new Mat(CAM_HEIGTH,CAM_WIDTH,CV_8U);
        
        Imgproc.remap(imgIzq, m_imagenRectificada1, mx1, my1,Imgproc.INTER_LINEAR);
        Imgproc.remap(imgDer, m_imagenRectificada2, mx2, my2,Imgproc.INTER_LINEAR);
        
        StereoBM  steroBM =  new StereoBM(BASIC_PRESET, 32, 41 );
        steroBM.compute(imgIzq, imgDer, m_imagenProfundidad );
        Core.normalize(m_imagenProfundidad, m_imagenProfundidadRectificada,0,32,NORM_MINMAX);        
    }

    public Mat dameImagenProcesada()
    {
       return  m_imagenProfundidadRectificada;
    }

    public void dameBlobsimagenProcesada()
    {

    }

    public void dameDistanciaAZona()
    {

    }
    
    
}

