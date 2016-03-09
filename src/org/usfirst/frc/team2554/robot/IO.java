package org.usfirst.frc.team2554.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.*;
public class IO {
	final public static int launchButtonNumber = 4;
	final public static int autoAimButtonNumber = 2;
	final public static int LYAxisNumber = 1; //1 is the L Y Axis
	final public static int RYAxisNumber = 5; //5 is the R Y Axis
	final public static int rightTriggerNumber = 3; //3 is Right Trigger
	final public static int leftTriggerNumber = 4; //4 is Left Trigger
	public static Joystick rightStick = new Joystick(0); //The joystick for driving. Permanently binded to USB port 0
	public static Joystick controller = new Joystick(1); //The joystick for moving the arms and shooting. Permanently binded to USB port 1
}
