/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sterocalibration;

import boofcv.abst.feature.disparity.StereoDisparity;
import boofcv.alg.distort.ImageDistort;
import boofcv.alg.geo.PerspectiveOps;
import boofcv.alg.geo.RectifyImageOps;
import boofcv.alg.geo.rectify.RectifyCalibrated;
import boofcv.factory.feature.disparity.DisparityAlgorithms;
import boofcv.factory.feature.disparity.FactoryStereoDisparity;
import boofcv.struct.calib.StereoParameters;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;
import georegression.struct.se.Se3_F64;
import org.ejml.data.DenseMatrix64F;

/**
 *
 * @author duxman
 */
public class CDisparity
{
    
    /**
	 * Computes the dense disparity between between two stereo images.  The input images
	 * must be rectified with lens distortion removed to work!  Floating point images
	 * are also supported.
	 *
	 * @param rectLeft Rectified left camera image
	 * @param rectRight Rectified right camera image
	 * @param regionSize Radius of region being matched
	 * @param minDisparity Minimum disparity that is considered
	 * @param maxDisparity Maximum disparity that is considered
	 * @return Disparity image
	 */
	public static ImageUInt8 denseDisparity( ImageUInt8 rectLeft , ImageUInt8 rectRight ,int regionSize, int minDisparity , int maxDisparity )
	{
		// A slower but more accuracy algorithm is selected
		// All of these parameters should be turned
		StereoDisparity<ImageUInt8,ImageUInt8> disparityAlg =
				FactoryStereoDisparity.regionWta(DisparityAlgorithms.RECT_FIVE,
						minDisparity, maxDisparity, regionSize, regionSize, 25, 1, 0.2, ImageUInt8.class);
 
		// process and return the results
		disparityAlg.process(rectLeft,rectRight);
 
		return disparityAlg.getDisparity();
	}
 
	/**
	 * Same as above, but compute disparity to within sub-pixel accuracy. The difference between the
	 * two is more apparent when a 3D point cloud is computed.
	 */
	public static ImageFloat32 denseDisparitySubpixel( ImageUInt8 rectLeft , ImageUInt8 rectRight ,int regionSize ,int minDisparity , int maxDisparity )
	{
		// A slower but more accuracy algorithm is selected
		// All of these parameters should be turned
		StereoDisparity<ImageUInt8,ImageFloat32> disparityAlg =
				FactoryStereoDisparity.regionSubpixelWta(DisparityAlgorithms.RECT_FIVE,
						minDisparity, maxDisparity, regionSize, regionSize, 25, 1, 0.2, ImageUInt8.class);
 
		// process and return the results
		disparityAlg.process(rectLeft,rectRight);
 
		return disparityAlg.getDisparity();
	}
 
	/**
	 * Rectified the input images using known calibration.
	 */
	public static RectifyCalibrated rectify( ImageUInt8 origLeft , ImageUInt8 origRight , StereoParameters param , ImageUInt8 rectLeft , ImageUInt8 rectRight )
	{
		// Compute rectification
		RectifyCalibrated rectifyAlg = RectifyImageOps.createCalibrated();
		Se3_F64 leftToRight = param.getRightToLeft().invert(null);
 
		// original camera calibration matrices
		DenseMatrix64F K1 = PerspectiveOps.calibrationMatrix(param.getLeft(), null);
		DenseMatrix64F K2 = PerspectiveOps.calibrationMatrix(param.getRight(), null);
 
		rectifyAlg.process(K1,new Se3_F64(),K2,leftToRight);
 
		// rectification matrix for each image
		DenseMatrix64F rect1 = rectifyAlg.getRect1();
		DenseMatrix64F rect2 = rectifyAlg.getRect2();
		// New calibration matrix,
		DenseMatrix64F rectK = rectifyAlg.getCalibrationMatrix();
 
		// Adjust the rectification to make the view area more useful
		RectifyImageOps.allInsideLeft(param.left, rect1, rect2, rectK);
 
		// undistorted and rectify images
		ImageDistort<ImageUInt8,ImageUInt8> imageDistortLeft =
				RectifyImageOps.rectifyImage(param.getLeft(), rect1, ImageUInt8.class);
		ImageDistort<ImageUInt8,ImageUInt8> imageDistortRight =
				RectifyImageOps.rectifyImage(param.getRight(), rect2, ImageUInt8.class);
 
		imageDistortLeft.apply(origLeft, rectLeft);
		imageDistortRight.apply(origRight, rectRight);
 
		return rectifyAlg;
	}
 	
}
