package duxmancar.Raspberry.Hardware.Sensores.Vision;


import duxmancar.Raspberry.Hardware.Sensores.CSensor;
import duxmancar.Raspberry.Hardware.Sensores.Vision.CObstaculo.eSimbolo;
import duxmancar.util.IDatosGenerales;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
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
public class  CCamara extends CSensor
{

    protected VideoCapture m_camara;
    private int m_iIdCamara = IDatosGenerales.NINGUNO;
    Mat m_ultimaImagen;
    Mat m_ultimaImagenGris;
    
    protected static CCamara instance = null;

    private  CCamara()
    {
        m_ultimaImagen= new Mat();
        m_ultimaImagenGris= new Mat();
    }
    
    public static CCamara getInstance()
    {
        if(instance ==  null)
        {
            instance = new CCamara(); 
        }
        
        return instance;
    }
    
    public int iniciaCamara(int iId)
    {
        int iRtn = 1;
        
        try
        {
            if( m_iIdCamara == IDatosGenerales.NINGUNO )
            {
                m_camara = new VideoCapture(iId);

                if (m_camara.isOpened() == true)
                {
                    m_camara.set(CAP_PROP_FRAME_WIDTH, 320);
                    m_camara.set(CAP_PROP_FRAME_HEIGHT, 240);
                    m_iIdCamara = iId;
                    iRtn = 1;
                }
                else
                {
                    iRtn = 0;
                }
            }
            else if ( m_camara.isOpened() && m_iIdCamara != IDatosGenerales.NINGUNO )
            {
                iRtn = 1;
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
        
        Imgproc.erode(m_ultimaImagenGris, m_ultimaImagenGris,new Mat());
        Imgproc.dilate(m_ultimaImagenGris, m_ultimaImagenGris,new Mat());
        Imgproc.Canny(m_ultimaImagenGris, m_ultimaImagenGris, 5, 70);        
        
        Imgproc.HoughCircles(m_ultimaImagenGris, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 1 ,200,100,0,0);
        
        System.out.println(circles);
        
        boolean izq = false ,der= false ,cen= false;
        
        for (int i = 0; i < circles.cols(); i++)
        {
            double[] circle = circles.get(0, i);
            Point pCentro = new Point(circle[0], circle[1]);                                    
            ponObstaculos( pCentro , eSimbolo.OTRO );                             
        }                      
    }
    
    private void ponObstaculos(Point pCentro, eSimbolo simbol)
    {
        eSimbolo izq = CObstaculo.hayObstaculoIzquierda();
        eSimbolo der=  CObstaculo.hayObstaculoDerecha();
        eSimbolo cen=  CObstaculo.hayObstaculoCentro();
        
        if( pCentro.x > 0 && pCentro.x <= CObstaculo.PIXEL_IZQ  && izq == eSimbolo.NONE )
            izq = simbol;
            
        if( pCentro.x > CObstaculo.PIXEL_IZQ && pCentro.x <= CObstaculo.PIXEL_CEN && cen == eSimbolo.NONE )
            cen = simbol;
            
        if( pCentro.x > CObstaculo.PIXEL_CEN && pCentro.x <= CObstaculo.PIXEL_DER && der == eSimbolo.NONE )
            der = simbol;    
        
        CObstaculo.setObstaculo(izq, cen, der);
    }
    
    private Point dameCentro(Rect rect)
    {         
        int rX = (rect.width/2) + rect.x;
        int rY = (rect.height/2) + rect.y;
        Point retorno = new Point(rX,rY);
        return retorno;
        
    }
    
    public boolean detectarForma(String sficheroHarr, eSimbolo simbol)
    {
        boolean retorno = false;
        capturarImagen();
        CascadeClassifier DetectorCirculos = new CascadeClassifier(sficheroHarr);
        
        MatOfRect Detections = new MatOfRect();
        DetectorCirculos.detectMultiScale(m_ultimaImagen, Detections);
        
         for (Rect rect : Detections.toArray()) 
         {
            retorno = true;
            ponObstaculos( dameCentro(rect), simbol );
            //Core.rectangle(m_ultimaImagen, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
         }
         return retorno;        
    }
    
    public boolean detectaColor(int iLowH, int iLowS, int iLowV , int iHighH ,int iHighS, int iHighV)
    {
         boolean retorno = false;
         Mat imgHSV = new Mat();
         Mat imgProcesada = new Mat();
         capturarImagen();
         Scalar scalarLow = new Scalar(iLowH,iLowS, iLowV,0);
         Scalar scalarHigh = new Scalar(iHighH,iHighS, iHighV,0);
                 
         Imgproc.cvtColor(m_ultimaImagen,imgHSV, Imgproc.COLOR_RGB2HSV);
         Core.inRange(imgHSV, scalarLow,  scalarHigh, imgProcesada);
         
         Imgproc.erode(imgProcesada, imgProcesada, new Mat() );
         Imgproc.dilate(imgProcesada, imgProcesada, new Mat() ,new Point(5, 5), 3);  
         if( buscarMayorContorno( imgProcesada ) )
         {
             retorno=true;
         }
         return retorno;         
    }
    
    
    private boolean buscarMayorContorno(Mat img)
    {        
        boolean retorno = false;
        List<MatOfPoint> contorno = new ArrayList<>();
        int iAreaMin = -1;
        Blobs m_Regions = new Blobs();
        m_Regions.BlobAnalysis( img,
                              -1, -1,                     // ROI start col, row
                              -1, -1,                     // ROI cols, rows
                               1,                         // border (0 = black; 1 = white)
                               100);     
        
        m_log.info( m_Regions.PrintRegionData() );
        
        for(int i = 1; i <= m_Regions.MaxLabel; i++)
        {
            double [] Region = m_Regions.RegionData[i];
            int Parent = (int) Region[Blobs.BLOBPARENT];
            int Color = (int) Region[Blobs.BLOBCOLOR];
            int MinX = (int) Region[Blobs.BLOBMINX];
            int MaxX = (int) Region[Blobs.BLOBMAXX];
            int MinY = (int) Region[Blobs.BLOBMINY];
            int MaxY = (int) Region[Blobs.BLOBMAXY]; 
            int area = (int) Region[Blobs.BLOBAREA];            
            if( ( MinX == 0 && MinY == 0 ) && ( MaxX >= ( img.cols() * 0.75 ) && MaxY >= ( img.rows() * 0.75 ) ) )
            {
                m_log.info("Borde de la imagen ignorado");
                m_Regions.ResetRegion(i);
            }
            else
            {
                if( iAreaMin < area)
                {
                    
                    iAreaMin = area;
                    retorno=true;
                    Rect r = new Rect(MinX,MinY,MaxX-MinX,MaxY-MinY);
                    ponObstaculos( dameCentro(r), eSimbolo.OTRO );
                }
                             
            }
        }
        return retorno;
    }

    @Override public void medir() throws Exception
    {
        throw new Exception("No es posible invocar la medición desde esta clase "); 
    }
}

