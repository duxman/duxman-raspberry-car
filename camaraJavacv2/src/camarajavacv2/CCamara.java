/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camarajavacv2;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;
import static org.bytedeco.javacpp.helper.opencv_core.cvDrawContours;
import static org.bytedeco.javacpp.helper.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_core.CV_AA;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Point;
import static org.bytedeco.javacpp.opencv_core.cvCircle;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_core.cvDrawCircle;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvLoad;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import org.bytedeco.javacpp.opencv_imgproc;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2HSV;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_COUNTER_CLOCKWISE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_MOP_OPEN;
import static org.bytedeco.javacpp.opencv_imgproc.CV_POLY_APPROX_DP;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_LIST;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2HSV;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY_INV;
import static org.bytedeco.javacpp.opencv_imgproc.cvApproxPoly;
import static org.bytedeco.javacpp.opencv_imgproc.cvConvexHull2;
import static org.bytedeco.javacpp.opencv_imgproc.cvConvexityDefects;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvGetCentralMoment;
import static org.bytedeco.javacpp.opencv_imgproc.cvGetSpatialMoment;
import static org.bytedeco.javacpp.opencv_imgproc.cvMinAreaRect2;
import static org.bytedeco.javacpp.opencv_imgproc.cvMoments;
import static org.bytedeco.javacpp.opencv_imgproc.cvMorphologyEx;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

/**
 *
 * @author duxman
 */
public class CCamara
{
    private static final float SMALLEST_AREA = 600.0f;    // was 100.0f;
    // ignore smaller contour areas

    private static final int MAX_POINTS = 20;   // max number of points stored in an array

    // used for simiplifying the defects list
    private static final int MIN_FINGER_DEPTH = 20;
    private static final int MAX_FINGER_ANGLE = 60;   // degrees

    // angle ranges of thumb and index finger of the left hand relative to its COG
    private static final int MIN_THUMB = 120;
    private static final int MAX_THUMB = 200;

    private static final int MIN_INDEX = 60;
    private static final int MAX_INDEX = 120;
    
    
    OpenCVFrameConverter.ToIplImage m_converterIpl;

    OpenCVFrameGrabber m_grabber;
    IplImage m_imagen;
    IplImage m_imagenHSV;
    IplImage m_imagenThreshold;
    CvMemStorage contourStorage, approxStorage, hullStorage, defectsStorage;
    private List<Point> listaDedos ;
    private Point puntoCentral; 
    private int contourAxisAngle;
       
    private Point[] PuntaDedos, UnionDedos;
    private float[] Profundidad;
    

    BufferedImage m_imagenNormal;

    public CCamara()
    {
        m_grabber = init(0);
        if (m_grabber == null)
        {
            return;
        }
        m_imagen = IplImage.create(320, 240, 8, 3);
        m_imagenHSV = IplImage.create(320, 240, 8, 3);
        m_imagenThreshold = IplImage.create(320, 240, 8, 1);
        
        contourStorage = CvMemStorage.create();
        approxStorage = CvMemStorage.create();
        hullStorage = CvMemStorage.create();
        defectsStorage = CvMemStorage.create();
        
        listaDedos = new ArrayList<>();
        puntoCentral = new Point();
        
        PuntaDedos = new Point[MAX_POINTS];   // coords of the finger tips
        UnionDedos = new Point[MAX_POINTS];  // coords of the skin folds between fingers
        Profundidad = new float[MAX_POINTS]; 
        
        inicializaVentana();
    }

    private void inicializaVentana()
    {
    }

    private OpenCVFrameGrabber init(int ID)
    {
        OpenCVFrameGrabber grabber = null;
        System.out.println("Initializing grabber for ... "); //+ videoInput.getDeviceName(ID) + " ...");
        try
        {
            grabber = new OpenCVFrameGrabber(ID);
            //grabber = OpenCVFrameGrabber.createDefault(ID);
            grabber.setFormat("dshow");       // using DirectShow
            grabber.setImageWidth(320);     // default is too small: 320x240
            grabber.setImageHeight(240);
            m_converterIpl = new OpenCVFrameConverter.ToIplImage();

            grabber.start();

        }
        catch (Exception e)
        {
            System.out.println("Could not start grabber");
            System.out.println(e);
            System.exit(1);
        }
        return grabber;
    }  // end of initGrabber()

    public void capturar() throws Exception
    {
        m_imagen = m_converterIpl.convert(m_grabber.grab());
       
    }

    public IplImage procesar(IplImage imImagen,int iLH, int iLS, int iLV, int iHH, int iHS, int iHV)
    {
        cvCvtColor(imImagen, m_imagenHSV, CV_RGB2HSV);
        cvInRangeS(m_imagenHSV, cvScalar(iLH, iLS, iLV, 0), cvScalar(iHH, iHS, iHV, 0), m_imagenThreshold);
        cvMorphologyEx(m_imagenThreshold, m_imagenThreshold, null, null, CV_MOP_OPEN, 5);        
        return m_imagenThreshold;
    }
    
    public IplImage cascade()
    {
       	CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad("cascadecirculo.xml")); 
        CvMemStorage storage = CvMemStorage.create();
        CvSeq sign = cvHaarDetectObjects(
            m_imagen,
            cascade,
            storage,
            1.5,
            3,
            CV_HAAR_DO_CANNY_PRUNING); 
        
        cvClearMemStorage(storage);
        int total_Faces = sign.total();
                
        for(int i = 0; i < total_Faces; i++)
        {
            CvRect r = new CvRect(cvGetSeqElem(sign, i));
            opencv_core.cvRectangle(m_imagen,
                            cvPoint(r.x(), r.y()), 
                            cvPoint(r.width() + r.x( ), r.height() + r.y()),
                            opencv_core.CvScalar.RED,2,CV_AA,0);
        }
        return m_imagen;      
    }       
    
    private IplImage crearImagenValida( int iLH, int iLS, int iLV, int iHH, int iHS, int iHV  )
    {
        IplImage retorno = IplImage.create(320, 240, 8, 1);
        cvThreshold(retorno, retorno, 230, 255, CV_THRESH_BINARY);
        
        IplImage imagenThreshold = IplImage.create(320, 240, 8, 1);
        IplImage imagenHSV = IplImage.create(320, 240, 8, 3);
        
        cvCvtColor(m_imagen, imagenHSV, CV_BGR2HSV);
        cvInRangeS(imagenHSV, cvScalar(iLH, iLS, iLV, 0), cvScalar(iHH, iHS, iHV, 0), imagenThreshold);
        cvMorphologyEx(m_imagenThreshold, m_imagenThreshold, null, null, CV_MOP_OPEN, 2);
                
        opencv_core.CvSeq bigContour = buscarmayorContorno(imagenThreshold);
        if (bigContour == null)
        {
            return retorno;
        }
        cvDrawContours(retorno, bigContour, opencv_core.CvScalar.WHITE , opencv_core.CvScalar.WHITE, 1, -1, 8);         
        
        return retorno;
    }

    private void pintar()
    {
         cvDrawCircle(m_imagen, cvPoint(puntoCentral.x(),puntoCentral.y( )) ,4, opencv_core.CvScalar.GREEN ,2,8,0 );
        
        for(int i=0 ; i < listaDedos.size(); i++ )
        {
            Point p = listaDedos.get(i);
            cvDrawCircle(m_imagen, cvPoint(p.x(),p.y( )) ,4, opencv_core.CvScalar.YELLOW ,2,8,0 );
            org.bytedeco.javacpp.opencv_core.cvDrawLine(m_imagen,cvPoint(puntoCentral.x(),puntoCentral.y( )), cvPoint(p.x(),p.y( )),opencv_core.CvScalar.BLUE,2,8,0);
        }
    }
    private opencv_core.CvSeq buscarmayorContorno(IplImage imgThreshed)
    // return the largest contour in the threshold image
    {
        opencv_core.CvSeq bigContour = null;

        // generate all the contours in the threshold image as a list
        opencv_core.CvSeq contorno = new opencv_core.CvSeq(null);
        cvFindContours(imgThreshed, contourStorage, contorno, Loader.sizeof(opencv_core.CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);

        // find the largest contour in the list based on bounded box size
        float maxArea = -1;
        opencv_core.CvBox2D maxBox = null;
        while (contorno != null && !contorno.isNull())
        {
            if (contorno.elem_size() > 0)
            {
                opencv_core.CvBox2D box = cvMinAreaRect2(contorno, contourStorage);
                if (box != null)
                {
                    opencv_core.CvSize2D32f size = box.size();
                    float area = size.width() * size.height();
                    if (area > maxArea)
                    {
                        maxArea = area;
                        bigContour = contorno;
                    }
                }
            }
            contorno = contorno.h_next();
        }

        return bigContour;
    }

     // ----------------- analyze contour ----------------------------
    private void extractContourInfo(opencv_core.CvSeq bigContour)
    /* calculate COG and angle of the contour's main axis relative to the horizontal.
     Store them in the globals cogPt and contourAxisAngle
     */
    {
        opencv_imgproc.CvMoments moments = new opencv_imgproc.CvMoments();
        cvMoments(bigContour, moments, 1);     // CvSeq is a subclass of CvArr

        // center of gravity
        double m00 = cvGetSpatialMoment(moments, 0, 0);
        double m10 = cvGetSpatialMoment(moments, 1, 0);
        double m01 = cvGetSpatialMoment(moments, 0, 1);

        if (m00 != 0)
        {   // calculate center
            int xCenter = (int) Math.round(m10 / m00) ;
            int yCenter = (int) Math.round(m01 / m00) ;
            puntoCentral.put(xCenter, yCenter);
        }

        double m11 = cvGetCentralMoment(moments, 1, 1);
        double m20 = cvGetCentralMoment(moments, 2, 0);
        double m02 = cvGetCentralMoment(moments, 0, 2);
        contourAxisAngle = calculateTilt(m11, m20, m02);
        /* this angle assumes that the positive y-axis
         is down the screen */

        // deal with hand contour pointing downwards
    /* uses fingertips information generated on the last update of
         the hand, so will be out-of-date */
        if (listaDedos.size() > 0)
        {
            int yTotal = 0;
            for (Point pt : listaDedos)
            {
                yTotal += pt.y();
            }
            int avgYFinger = yTotal / listaDedos.size();
            if (avgYFinger > puntoCentral.y())   // fingers below COG
            {
                contourAxisAngle += 180;
            }
        }
        contourAxisAngle = 180 - contourAxisAngle;
        /* this makes the angle relative to a positive y-axis that
         runs up the screen */

        // System.out.println("Contour angle: " + contourAxisAngle);
    }  // end of extractContourInfo()
    
     private int calculateTilt(double m11, double m20, double m02)
    /* Return integer degree angle of contour's major axis relative to the horizontal, 
     assuming that the positive y-axis goes down the screen. 

     This code is based on maths explained in "Simple Image Analysis By Moments", by
     Johannes Kilian, March 15, 2001 (see Table 1 on p.7). 
     The paper is available at:
     http://public.cranfield.ac.uk/c5354/teaching/dip/opencv/SimpleImageAnalysisbyMoments.pdf
     */
    {
        double diff = m20 - m02;
        if (diff == 0)
        {
            if (m11 == 0)
            {
                return 0;
            }
            else if (m11 > 0)
            {
                return 45;
            }
            else   // m11 < 0
            {
                return -45;
            }
        }

        double theta = 0.5 * Math.atan2(2 * m11, diff);
        int tilt = (int) Math.round(Math.toDegrees(theta));

        if ((diff > 0) && (m11 == 0))
        {
            return 0;
        }
        else if ((diff < 0) && (m11 == 0))
        {
            return -90;
        }
        else if ((diff > 0) && (m11 > 0))  // 0 to 45 degrees
        {
            return tilt;
        }
        else if ((diff > 0) && (m11 < 0))  // -45 to 0
        {
            return (180 + tilt);   // change to counter-clockwise angle measure
        }
        else if ((diff < 0) && (m11 > 0))   // 45 to 90
        {
            return tilt;
        }
        else if ((diff < 0) && (m11 < 0))   // -90 to -45
        {
            return (180 + tilt);  // change to counter-clockwise angle measure
        }
        System.out.println("Error in moments for tilt angle");
        return 0;
    } 
     
       // ---------------- analyze fingers -------------------------
    private void findFingerTips(opencv_core.CvSeq bigContour)
    /* Starting with the contour, calculate its convex hull, and its
     convexity defects. Ignore defects that are unlikely to be fingers.
     */
    {
        opencv_core.CvSeq approxContour = cvApproxPoly(bigContour, Loader.sizeof(opencv_core.CvContour.class),
                approxStorage, CV_POLY_APPROX_DP, 3, 1);
        // reduce number of points in the contour

        opencv_core.CvSeq hullSeq = cvConvexHull2(approxContour, hullStorage, CV_COUNTER_CLOCKWISE, 0);
        // find the convex hull around the contour

        opencv_core.CvSeq defects = cvConvexityDefects(approxContour, hullSeq, defectsStorage);
        // find the defect differences between the contour and hull
        int defectsTotal = defects.total();
        if (defectsTotal > MAX_POINTS)
        {
            System.out.println("Only processing " + MAX_POINTS + " defect points");
            defectsTotal = MAX_POINTS;
        }

        // copy defect information from defects sequence into arrays
        for (int i = 0; i < defectsTotal; i++)
        {
            BytePointer pntr = cvGetSeqElem(defects, i);
            opencv_imgproc.CvConvexityDefect cdf = new opencv_imgproc.CvConvexityDefect(pntr);

            opencv_core.CvPoint startPt = cdf.start();
            PuntaDedos[i] = new Point((int) Math.round(startPt.x() ),(int) Math.round(startPt.y() ));
            // an array containing the coordinates of the finger tips

            opencv_core.CvPoint endPt = cdf.end();
            opencv_core.CvPoint depthPt = cdf.depth_point();
            UnionDedos[i] = new Point((int) Math.round(depthPt.x() ), (int) Math.round(depthPt.y() ));
            // an array containing the coordinates of the skin fold between fingers
            Profundidad[i] = cdf.depth();
            // an array containing the distances from tips to folds
        }

        reduceTips(defectsTotal, PuntaDedos, UnionDedos, Profundidad);
    }  // end of findFingerTips()

    private void reduceTips(int numPoints, Point[] tipPts, Point[] foldPts, float[] depths)
    /* Narrow in on 'real' finger tips by ignoring shallow defect depths, and tips
     which have too great an angle between their neighbouring fold points.

     Store the resulting finger tip coordinates in the global fingerTips list.
     */
    {
        listaDedos.clear();

        for (int i = 0; i < numPoints; i++)
        {
            if (depths[i] < MIN_FINGER_DEPTH)    // defect too shallow
            {
                continue;
            }

            // look at fold points on either side of a tip
            int pdx = (i == 0) ? (numPoints - 1) : (i - 1);   // predecessor of i
            int sdx = (i == numPoints - 1) ? 0 : (i + 1);     // successor of i
            int angle = angleBetween(tipPts[i], foldPts[pdx], foldPts[sdx]);
            if (angle >= MAX_FINGER_ANGLE)      // angle between finger and folds too wide
            {
                continue;
            }

            // this point probably is a finger tip, so add to list
            listaDedos.add(tipPts[i]);
        }
        // System.out.println("No. of finger tips: " + fingerTips.size());
    }  // end of reduceTips()

    private int angleBetween(Point tip, Point next, Point prev)
    // calulate the angle between the tip and its neigbouring folds (in integer degrees)
    {
        return Math.abs((int) Math.round(
                Math.toDegrees(
                        Math.atan2(next.x()- tip.x(), next.y()- tip.y())
                        - Math.atan2(prev.x()- tip.x(), prev.y()- tip.y()))));
    }
}
