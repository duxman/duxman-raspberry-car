/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jduxmancarvision;

import jduxmancarvision.OpenCV.Blobs;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import static org.opencv.highgui.Highgui.CV_CAP_PROP_FRAME_HEIGHT;
import static org.opencv.highgui.Highgui.CV_CAP_PROP_FRAME_WIDTH;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.video.BackgroundSubtractorMOG;
import org.opencv.video.BackgroundSubtractorMOG2;

/**
 *
 * @author duxman
 */
public class JVision extends Thread
{
    Logger m_log;
    VideoCapture camera;    
    JVentana ventana;
    Mat imagen0;
    Mat imagen1;
    Mat imagen;
    Mat imagenTMP;
    public boolean m_bCaras;
    public boolean m_bDetectar;
    public boolean m_bDetectar2;
    
     CascadeClassifier faceDetector1;
     CascadeClassifier faceDetector2;
     CascadeClassifier faceDetector3;
     MatOfRect faceDetections;
     public int m_ithresholValue;
     public int m_isizeValue;
     Random r;
     BackgroundSubtractorMOG back;

    public JVision()
    {       
        m_log = Logger.getRootLogger();   
        m_bCaras = false;        
        m_bDetectar = false;  
        r = new Random();
    }
    
    public void setVentana( JVentana v )
    {
        ventana = v;
    }
    
    public void init(  )
    {
        try
        {
            m_log.info("Inicializamos opencv");
            // Load the native library.
            System.load("/home/duxman/git/duxman-raspberry-car/JDuxmanCarVision/dist/lib/libopencv_java2410.so" );                        
            inicializaVideo();            
        }
        catch (InterruptedException ex)
        {
            
        }
    }
    
    public void inicializaVideo() throws InterruptedException
    {               
        camera = new VideoCapture(0);        
        
        boolean a = camera.set(CV_CAP_PROP_FRAME_WIDTH,320);
        boolean b = camera.set(CV_CAP_PROP_FRAME_HEIGHT,240);
        m_log.info(a);
        m_log.info(b);
        
        
        
        Thread.sleep(10000);
               
        if(!camera.isOpened())
        {
           m_log.info("Camera Error");
        }
        else
        {
            imagen0 = new Mat(); 
            imagen1 = new Mat(); 
            imagen = new Mat(); 
            imagenTMP = new Mat(); 
            initRunFace();
            
            m_log.info("Camera OK?");
        }        
    }
    
    public void initRunFace()
    {
        faceDetector1 = new CascadeClassifier();
        faceDetector2 = new CascadeClassifier();
        faceDetector3 = new CascadeClassifier();
        faceDetections = new MatOfRect();            
        
        if( !faceDetector1.load("/home/duxman/git/duxman-raspberry-car/JDuxmanCarVision/dist/lbpcascade_frontalface.xml")) 
            m_log.error("no se puede leer lbpcascade_frontalface");

        if( !faceDetector2.load("/home/duxman/git/duxman-raspberry-car/JDuxmanCarVision/dist/lbpcascade_profileface.xml"))
            m_log.error("no se puede leer lbpcascade_profileface");

        if( !faceDetector3.load("/home/duxman/git/duxman-raspberry-car/JDuxmanCarVision/dist/lbpcascade_silverware.xml"))
            m_log.error("no se puede leer lbpcascade_silverware");
    }
    
    public void run()
    {
        while(true)
        {
            camera.read(imagen0);              
             if(!imagen0.empty())
             {                               
                
                Imgproc.cvtColor(imagen0, imagenTMP, Imgproc.COLOR_RGB2GRAY);               
                try
                {
                    runFace();
                    detectarCosas();
                    detectarCosas2();
                    sleep(10);
                }
                catch (Exception ex)
                {
                    m_log.error("Error en el hilo "+ ex.getMessage() );
                }
                
                ventana.setImage(convertir(imagen0));
                ventana.setImage2(convertir(imagenTMP));
             }
            
            
        }
        
    }
    
    public void detectarCosasV1()
    {
       try
       { 
           if( m_bDetectar )  
           {  
              
              //Imgproc.adaptiveThreshold(imagenTMP,imagenTMP,250, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 35, 4);                            
                             
              Imgproc.threshold(imagenTMP, imagenTMP, m_ithresholValue, 255, Imgproc.THRESH_BINARY );              
              
              Imgproc.dilate(imagenTMP, imagenTMP, new Mat() ,new Point(-1, -1), m_isizeValue);
              for(int i = 0 ;i  < m_isizeValue; i++)
                    Imgproc.GaussianBlur(imagenTMP, imagenTMP, new  Size(m_isizeValue, m_isizeValue),m_isizeValue,m_isizeValue);               
              
              Imgproc.threshold(imagenTMP, imagenTMP, m_ithresholValue, 255, Imgproc.THRESH_BINARY );                                         

              
           }
       }
       catch(Exception e)
       {
           m_log.error("Error detectar " +e.getMessage());
       }
    }
    
     public void detectarCosas()
    {
       try
       { 
           if( m_bDetectar )  
           {  
             /*Imgproc.cvtColor(imagen0, imagenTMP, Imgproc.COLOR_BGR2HSV); 
             
             List<Mat> hsv_planes = new ArrayList<Mat>();
             Core.split(imagenTMP, hsv_planes);
             
             Mat h_hist = hsv_planes.get(0);             
             Mat s_hist = hsv_planes.get(1);
             Mat v_hist = hsv_planes.get(2);
                                            
           

            Imgproc.threshold(h_hist, h_hist, m_ithresholValue, 255, Imgproc.THRESH_BINARY );              
            Imgproc.erode(h_hist, h_hist, new Mat() );
            Imgproc.dilate(h_hist, h_hist, new Mat() ,new Point(-1, -1), 1);

            Imgproc.threshold(s_hist, s_hist, m_ithresholValue, 255, Imgproc.THRESH_BINARY );                           
            Imgproc.erode(s_hist, s_hist, new Mat() );
            Imgproc.dilate(s_hist, s_hist, new Mat() ,new Point(-1, -1), 1);

            Imgproc.threshold(v_hist, imagenTMP, m_ithresholValue, 255, Imgproc.THRESH_BINARY );    */
            Imgproc.threshold(imagenTMP, imagenTMP, m_ithresholValue, 255, Imgproc.THRESH_BINARY ); 
            Imgproc.erode(imagenTMP, imagenTMP, new Mat() );
            Imgproc.dilate(imagenTMP, imagenTMP, new Mat() ,new Point(-1, -1), 1);  

            //Core.merge(hsv_planes, imagenTMP);           
            //Imgproc.threshold(imagenTMP, imagenTMP, m_ithresholValue, 255, Imgproc.THRESH_BINARY_INV );     
                                                             
           }
       }
       catch(Exception e)
       {
           m_log.error("Error detectar " +e.getMessage());
       }
    }
    
     public void detectarCosas2()
    {
       try
       { 
           if( m_bDetectar2 )  
           {
             
               
               /* List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
                Imgproc.findContours(imagenTMP, contours, new Mat() , Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
                
                double maxArea = 0;
                int index = -1;
                for (MatOfPoint contour : contours) 
                { // iterate over every contour in the list
                    double area = Imgproc.contourArea(contour);
                    if (area > 50) 
                    {
                      Mat hierarchy = new Mat();
                      index = contours.indexOf(contour);                                            
                      Scalar Red = new Scalar( 0, 0, 255);
                      Scalar Blue = new Scalar( 0, 255, 0);
                      Scalar Green = new Scalar( 255, 0, 0);                     
                      
                      Imgproc.drawContours(imagen0, contours, index, Green,-1, 8,hierarchy,0,new Point(-5,-5)); // 255 = draw contours in white                                                                                  
                    }
                             
                }*/
                Blobs Regions = new Blobs();
                 Regions.BlobAnalysis(
                    imagenTMP,               // image
                    -1, -1,                     // ROI start col, row
                    -1, -1,                     // ROI cols, rows
                    1,                          // border (0 = black; 1 = white)
                    1000);     
                m_log.info( Regions.PrintRegionData() );
                
                for(int i = 1; i <= Blobs.MaxLabel; i++)
                {
                    double [] Region = Blobs.RegionData[i];
                    int Parent = (int) Region[Blobs.BLOBPARENT];
                    int Color = (int) Region[Blobs.BLOBCOLOR];
                    int MinX = (int) Region[Blobs.BLOBMINX];
                    int MaxX = (int) Region[Blobs.BLOBMAXX];
                    int MinY = (int) Region[Blobs.BLOBMINY];
                    int MaxY = (int) Region[Blobs.BLOBMAXY]; 
                    int area = (int) Region[Blobs.BLOBAREA];
                    if( ( MinX == 0 && MinY == 0 ) && ( MaxX >= ( imagenTMP.cols() * 0.75 ) && MaxY >= ( imagenTMP.rows() * 0.75 ) ) )
                    {
                        m_log.info("Borde de la imagen ignorado");
                    }
                    else
                    {
                        Core.rectangle(imagen0, new Point(MinX, MinY), new Point(MaxX , MaxY ), new Scalar(0, 255, 0),3);
                    }
                    
                }                                                                
           }
       }
       catch(Exception e)
       {
           m_log.error("Error detectar " +e.getMessage());
       }
    }
    
               
    public void runFace(  )
    {
        try
        {
            if( m_bCaras )
            {                                                         
                faceDetector1.detectMultiScale(imagen0, faceDetections);
                for (Rect rect : faceDetections.toArray()) 
                {
                    Core.rectangle(imagen0, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                    m_log.info("Cara detectada");                    
                }                                

                faceDetector2.detectMultiScale(imagen0, faceDetections);
                for (Rect rect : faceDetections.toArray()) 
                {
                    Core.rectangle(imagen0, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                    m_log.info("Cara detectada");
                }                                    

                faceDetector3.detectMultiScale(imagen0, faceDetections);
                for (Rect rect : faceDetections.toArray()) 
                {
                    Core.rectangle(imagen0, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                    m_log.info("Cara detectada");
                }                                                    
            }
        }
        catch(Exception e)
        {
            m_log.error("Error runface" + e.getMessage());
        }
        
    }
    
    private Image convertir(Mat imagen) 
    {
        MatOfByte matOfByte = new MatOfByte();
        Highgui.imencode(".jpg", imagen, matOfByte); 

        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;

        try 
        {

            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
        } 
        catch (Exception e) 
        {
         m_log.error(e.getMessage());
        }
        return (Image)bufImage;
    }
    
}
