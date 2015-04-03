/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jduxmancarvision;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;

/**
 *
 * @author duxman
 */
public class JVision
{
    Logger m_log;
    VideoCapture camera;
    JVentana ventana;
    
    public JVision()
    {       
        m_log = Logger.getRootLogger();        
        init();
    }
    
    public void init()
    {
        try
        {
            m_log.info("Inicializamos opencv");
            // Load the native library.
            System.load("/home/duxman/git/duxman-raspberry-car/JDuxmanCarVision/dist/lib/libopencv_java2410.so" );
            ventana = new JVentana();
            inicializaVideo();
        }
        catch (InterruptedException ex)
        {
            
        }
    }
    
    public void inicializaVideo() throws InterruptedException
    {               
        camera = new VideoCapture(0);
        Thread.sleep(10000);
               
        if(!camera.isOpened())
        {
           System.out.println("Camera Error");
        }
        else
        {
            System.out.println("Camera OK?");
        }
        run();
    }
    
    public void run()
    {
        CascadeClassifier faceDetector = new CascadeClassifier("/home/duxman/git/duxman-raspberry-car/JDuxmanCarVision/dist/lbpcascade_frontalface.xml");
        MatOfRect faceDetections = new MatOfRect();
        Mat imagen=new Mat();
        
        while(true)
        {
           camera.read(imagen);
            if(!imagen.empty())
            {
                faceDetector.detectMultiScale(imagen, faceDetections);
                for (Rect rect : faceDetections.toArray()) 
                {
                    Core.rectangle(imagen, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                }
                ventana.setImage(convertir(imagen));
            }
                    
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
            e.printStackTrace();
        }
        return (Image)bufImage;
    }
    
}
