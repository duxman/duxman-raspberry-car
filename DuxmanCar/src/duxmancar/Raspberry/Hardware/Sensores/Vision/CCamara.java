package duxmancar.Raspberry.Hardware.Sensores.Vision;


import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH;

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
        m_ultimaImagen= new Mat();
        m_ultimaImagenGris= new Mat();
    }
    
    public int iniciaCamara(int iId)
    {
        int iRtn = 1;
        try
        {
            m_camara = new VideoCapture(iId);

            if (m_camara.isOpened() == true)
            {
                m_camara.set(CAP_PROP_FRAME_WIDTH, 320);
                m_camara.set(CAP_PROP_FRAME_HEIGHT, 240);
                m_iIdCamara = iId;

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
        
        Imgproc.GaussianBlur( m_ultimaImagenGris, m_ultimaImagenGris, new Size(9, 9), 2, 2 );       
        
        Imgproc.erode(m_ultimaImagenGris, m_ultimaImagenGris,new Mat());
        Imgproc.dilate(m_ultimaImagenGris, m_ultimaImagenGris,new Mat());
        Imgproc.Canny(m_ultimaImagenGris, m_ultimaImagenGris, 5, 70);
        Imgproc.GaussianBlur( m_ultimaImagenGris, m_ultimaImagenGris, new Size(9, 9), 2, 2 );       
        
        Imgproc.HoughCircles(m_ultimaImagenGris, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 1 ,200,100,0,0);
        System.out.println(circles);

        for (int i = 0; i < circles.cols(); i++)
        {
            double[] circle = circles.get(0, i);
            Point pCentro = new Point(circle[0], circle[1]);            
            Imgproc.circle(m_ultimaImagen, pCentro, (int) circle[2]- 10 , new Scalar(0, 255, 0),10);
        }
        return m_ultimaImagen;
    }
    
     public void detectarCirculos()
    {
        capturarImagen();
        Mat circles = new Mat();        
        
        Imgproc.GaussianBlur( m_ultimaImagenGris, m_ultimaImagenGris, new Size(9, 9), 2, 2 );       
        
        Imgproc.erode(m_ultimaImagenGris, m_ultimaImagenGris,new Mat());
        Imgproc.dilate(m_ultimaImagenGris, m_ultimaImagenGris,new Mat());
        Imgproc.Canny(m_ultimaImagenGris, m_ultimaImagenGris, 5, 70);
        Imgproc.GaussianBlur( m_ultimaImagenGris, m_ultimaImagenGris, new Size(9, 9), 2, 2 );       
        
        Imgproc.HoughCircles(m_ultimaImagenGris, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 1 ,200,100,0,0);
        System.out.println(circles);
        
        boolean izq = false ,der= false ,cen= false;
        
        for (int i = 0; i < circles.cols(); i++)
        {
            double[] circle = circles.get(0, i);
            Point pCentro = new Point(circle[0], circle[1]);            
            
            
            if( pCentro.x > 0 && pCentro.x <= CCirculos.PIXEL_IZQ  && izq == false )
                izq = true;
            
            if( pCentro.x > CCirculos.PIXEL_IZQ && pCentro.x <= CCirculos.PIXEL_CEN && cen == false )
                cen = true;
            
            if( pCentro.x > CCirculos.PIXEL_CEN && pCentro.x <= CCirculos.PIXEL_DER && der == false )
                der = true;                                
        }
        
        CCirculos.setCirculo(izq, cen, der);
        
    }
}

