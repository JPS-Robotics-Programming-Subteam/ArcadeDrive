package org.usfirst.frc.team2554.robot;

import edu.wpi.first.wpilibj.image.*;
import edu.wpi.first.wpilibj.vision.*;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team2554.robot.Robot;
import java.lang.Math;

public class Camera {
	//Limits for the HSL Threshold
	final private static int hueLow = 0;
	final private static int hueHigh = 0;
	final private static int saturationLow = 0;
	final private static int saturationHigh = 0;
	final private static int luminenceLow = 0;
	final private static int luminenceHigh = 0;
	//Values for Analysis
	static private double particleArea, boundArea, boundHeight, boundWidth;
	//Best report
	static private int bestReportNum;
	//How close the Values are to desired ones. Goes up linearly. Best close to 0.
	static private double areaComp, lengthComp, bestSumComp;
	static private AxisCamera axisCam;
	static private HSLImage originalImage;
	static private BinaryImage maskedImage, erodedImage;
	//Number of Erosions
	final private static  int erosions = 3;
	static private ParticleAnalysisReport originalreport[];
	//The point where the center of the goal is
	static double xCoord, yCoord;
	//Center of Screen/Where the ball shoots
	static private double xScreenCoord, yScreenCoord, acceptableRegion, xAdjustment;
	public Camera()
	{
	}
	public static void getCenterValues() throws NIVisionException
	{
		bestSumComp = 99999999999999.0;
		xCoord = 320/640;
		yCoord = 240/480 + xAdjustment(); 
		acceptableRegion = 10/480;
		axisCam = new AxisCamera("10.25.54.11");
		axisCam.getImage(originalImage);
		//Masks Image so only stuff over a certain threshold shows up.
		maskedImage = originalImage.thresholdHSL(hueLow, hueHigh, saturationLow, saturationHigh, luminenceLow, luminenceHigh);
		//Erodes the image so the smaller objects disappear. Decreases size of the array of Particle Analysis Reports
		erodedImage = maskedImage.removeSmallObjects(false, erosions);
		//Gets reports for all of the calculated particles
		originalreport = erodedImage.getOrderedParticleAnalysisReports();
		//Runs through to generate the best report
		for(int i = 0; i <originalreport.length;i ++)
		{
			particleArea = originalreport[i].particleArea;
			boundHeight = originalreport[i].boundingRectHeight;
			boundWidth = originalreport[i].boundingRectWidth;
			boundArea = boundHeight * boundWidth;
			areaComp = Math.abs(1/3.0-particleArea/boundArea);
			lengthComp = Math.abs(1.6-boundWidth/boundHeight);
			//Finds best report/lowest SumComp
			if((areaComp+lengthComp) < bestSumComp)
				bestReportNum = i;
		}
		//The values are flipped because the camera is sideways
		xCoord = originalreport[bestReportNum].center_mass_y_normalized;
		yCoord = originalreport[bestReportNum].center_mass_x_normalized;
		originalImage.free();
		maskedImage.free();
		erodedImage.free();
	}
	public static void adjustShooter() //May need to switch x's and y's if using Life Cam instead of sideways Axis Cam
	{
		xScreenCoord = yScreenCoord = 0;
		if((xScreenCoord > xCoord + xAdjustment()+acceptableRegion))
		{
			Robot.armShooter.set(0.1); //Values need to be adjusted
		}
		if((xScreenCoord > xCoord - (xAdjustment()+acceptableRegion)/2))
		{
			Robot.armShooter.set(0.1); //Values need to be adjusted
		}
		if((yScreenCoord>yCoord+acceptableRegion))
		{
			Robot.myRobot.arcadeDrive(0,0.1); //Values need to be adjusted
		}
		if((yScreenCoord<yCoord-acceptableRegion)){
			Robot.myRobot.arcadeDrive(0,-0.1); //Values need to be adjusted
		}
	}
	public static double xAdjustment()
	{
		xAdjustment = 40/480;
		return xAdjustment;
	}
}
