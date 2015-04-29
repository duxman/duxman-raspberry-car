/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sterocalibration;

import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.image.ShowImages;
import boofcv.gui.image.VisualizeImageData;
import boofcv.io.UtilIO;
import boofcv.io.image.UtilImageIO;
import boofcv.misc.BoofMiscOps;
import boofcv.struct.calib.StereoParameters;
import boofcv.struct.image.ImageUInt8;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import static sterocalibration.CDisparity.denseDisparity;
import static sterocalibration.CDisparity.rectify;

/**
 *
 * @author aduce
 */
public class SteroCalibration
{
  /**
   * Example of how to calibrate a stereo camera system using a planar calibration grid given a set of images.
   * Intrinsic camera parameters are estimated for both cameras individually, then extrinsic parameters
   * for the two cameras relative to each other are found   This example does not rectify the images, which is
   * required for some algorithms. See {@link boofcv.examples.stereo.ExampleRectifyCalibratedStereo}. Both square grid and chessboard targets
   * are demonstrated in this example. See calibration tutorial for a discussion of different target types and how to
   * collect good calibration images.
   *
   * All the image processing and calibration is taken care of inside of {@link CalibrateStereoPlanar}.  The code below
   * loads calibration images as inputs, calibrates, and saves results to an XML file.  See in code comments for tuning
   * and implementation issues.
   *
   * @see boofcv.examples.stereo.ExampleRectifyCalibratedStereo
   * @see CalibrateStereoPlanar
   *
   * @author Peter Abeles
   */
 
   
   /**
   * @param args the command line arguments
   */
    public static void main( String args[] ) 
    {
  /*    CCalibracion alg = new CCalibracion (args[0], "der", "izq");

      // Select which set of targets to use
      alg.setupBumblebeeChess(10,7,32);
  //		alg.setupBumblebeeSquare();

      // compute and save results
      alg.process(args[0]+"/calibracion.xml");
      */
      StereoParameters param = UtilIO.loadXML(args[0]+"/calibracion.xml");
      
       List<String> left =  BoofMiscOps.directoryList (args[0], "izq");
       List<String> right = BoofMiscOps.directoryList (args[0], "der");
       // ensure the lists are in the same order
       Collections.sort (left);
       Collections.sort (right);
       BufferedImage l = UtilImageIO.loadImage (left.get (0));
       BufferedImage r = UtilImageIO.loadImage (right.get (0));
       
       ImageUInt8 distLeft = ConvertBufferedImage.convertFrom(l,(ImageUInt8)null);
       ImageUInt8 distRight = ConvertBufferedImage.convertFrom(r,(ImageUInt8)null);
       // rectify images
       ImageUInt8 rectLeft = new ImageUInt8(distLeft.width,distLeft.height);
       ImageUInt8 rectRight = new ImageUInt8(distRight.width,distRight.height);
       
       CDisparity disparidad  = new CDisparity();
       disparidad.rectify(distLeft,distRight,param,rectLeft,rectRight);
       // compute disparity
       ImageUInt8 disparity = disparidad.denseDisparity(distLeft,distRight,1,0,250);
       // show results
       BufferedImage visualized = VisualizeImageData.disparity(disparity, null,100,150,0);
 
       ShowImages.showWindow(rectLeft,"Rectified L");
       ShowImages.showWindow(rectRight,"Rectified R");
       ShowImages.showWindow(distLeft,"DIS L");
       ShowImages.showWindow(distRight,"Dis R");
       ShowImages.showWindow(visualized,"Disparity");
    }         
}
