package detectarcirculos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author duxman
 */
public class CCamara
{

    protected VideoCapture m_camara;
    private int m_iIdCamara;
    Mat m_ultimaImagen;
    Mat m_ultimaImagenGris;

    public CCamara()
    {
        m_ultimaImagen = new Mat();
        m_ultimaImagenGris = new Mat();
    }

    public int iniciaCamara(int iId)
    {
        int iRtn = 1;
        try
        {
            m_camara = new VideoCapture(iId);

            if (m_camara.isOpened() == true)
            {
                m_iIdCamara = iId;
                m_camara.set(Highgui.CV_CAP_PROP_FRAME_WIDTH,320);
                m_camara.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, 240);

            }
            else
            {
                iRtn = 0;
            }
        }
        catch (Exception e)
        {
            iRtn = 0;
        }
        finally
        {
            return iRtn;
        }
    }

    public Mat capturarImagenVideo()
    {
        Mat Rtn = new Mat();
        m_camara.read(Rtn);
        return Rtn;
    }

    public Mat capturarImagen()
    {
        try
        {
            m_camara.read(m_ultimaImagen);
            if (m_ultimaImagen.empty() == false)
            {
                Imgproc.cvtColor(m_ultimaImagen, m_ultimaImagenGris, Imgproc.COLOR_RGB2GRAY);
            }
        }
        catch (Exception e)
        {

        }
        finally
        {
            return m_ultimaImagen;
        }
    }

    public Mat dameImagen()
    {
        return m_ultimaImagen;
    }

    public Mat dameImagenGris()
    {

        return m_ultimaImagenGris;
    }

    public Mat detectarCirculosDibujo()
    {
        capturarImagen();
        Mat circles = new Mat();

        Imgproc.GaussianBlur(m_ultimaImagenGris, m_ultimaImagenGris, new Size(9, 9), 2, 2);

        Imgproc.erode(m_ultimaImagenGris, m_ultimaImagenGris, new Mat());
        Imgproc.dilate(m_ultimaImagenGris, m_ultimaImagenGris, new Mat());
        Imgproc.Canny(m_ultimaImagenGris, m_ultimaImagenGris, 5, 70);
        Imgproc.GaussianBlur(m_ultimaImagenGris, m_ultimaImagenGris, new Size(9, 9), 2, 2);

        Imgproc.HoughCircles(m_ultimaImagenGris, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 1, 200, 100, 0, 0);
        System.out.println(circles);

        for (int i = 0; i < circles.cols(); i++)
        {
            double[] circle = circles.get(0, i);
            Point pCentro = new Point(circle[0], circle[1]);
            Core.circle(m_ultimaImagen, pCentro, (int) circle[2] - 10, new Scalar(0, 255, 0), 10);
        }
        return m_ultimaImagen;
    }

    public Mat detectarCirculos2(String titulo,String sCascade)
    {
        capturarImagen();
        return detectarCirculos2(titulo,sCascade,m_ultimaImagen);                
    }
    
    public Mat detectarCirculos2(String titulo , String sCascade, Mat img)
    {
        
        CascadeClassifier DetectorCirculos = new CascadeClassifier(sCascade);
        
        MatOfRect Detections = new MatOfRect();
        DetectorCirculos.detectMultiScale(img, Detections);
        
         for (Rect rect : Detections.toArray()) 
         {
            Core.rectangle(m_ultimaImagen, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255,0, 0), 3 );
            Core.putText(m_ultimaImagen, titulo, new Point(rect.x, rect.y), Core.FONT_HERSHEY_COMPLEX_SMALL, 1.5,new Scalar(255,0, 0), 1 ) ;
            
         }
         return m_ultimaImagen;
        
    }
    
}
