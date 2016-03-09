package org.usfirst.frc.team2554.robot;
/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    public static int leftMotorPort = 3; //The PWM port for the left two motors
    public static int rightMotorPort = 1; //The PWM port for the right two motors
    public static int shooterPorts[] = {4,5}; //List of PWM ports used for shooter
    public static int armBarPort = 6; //The PWM port for the arm bar
    public static int shooterBarPort = 7; //The PWM port for the shooter bar
    public static int launcherPort = 8; //The PWM port for the launcher
    public static int rollerPort = 2; //The PWM port for the roller
    public static int extensionPort = 0; //The PWM port for the extension(not used currently)
    public static int distanceSensorPort = 0; //The AnalogI/O port for the distance sensor
    public static String lifeCam1 = "cam0";
    public static String lifeCam2 = "cam2";
}
