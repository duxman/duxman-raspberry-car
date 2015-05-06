/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jduxmancarvision.OpenCV;

import org.apache.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.rectangle;

/**
 *
 * @author duxman
 */
public class CFuncionesBasicasCamara implements IBasicoVideo
{
    protected Logger m_log;
    protected Size m_partnerSize;
    
    protected MatOfPoint3f dameListaPuntos3D()
    {
     
        int cnt = 0;
        Point3[] vp = new Point3[(int) (m_partnerSize.height * m_partnerSize.width)];
        for (int i = 0; i < m_partnerSize.height; i++)
        {
            for (int j = 0; j < m_partnerSize.width; j++)
            {
                vp[cnt] = new Point3(i * TAM_CUADRADO, j * TAM_CUADRADO, 0);
                cnt++;
            }
        }        
        MatOfPoint3f corners3f = new MatOfPoint3f(vp);
        return corners3f;
    }
    
    protected Mat DrawCircle( Mat imagen )
    {
        Mat rtn = new Mat();
        circle(imagen, new Point(5,5), 4, new Scalar(255,0,0),4);
        imagen.copyTo(rtn);
        return rtn;
    }
    
    protected Mat DrawSquare( Mat imagen )
    {
        Mat rtn = new Mat();
        rectangle(imagen, new Point(1,1), new Point(5,5), new Scalar(0,255,0),4);
        imagen.copyTo(rtn);
        return rtn;
    }
}
