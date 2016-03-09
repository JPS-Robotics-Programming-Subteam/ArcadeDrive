package org.usfirst.frc.team2554.robot;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2554.commands.*;
/**
 * This is a demo program showing the use of the RobotDrive class.
 * The SampleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're inexperienced,
 * don't. Unless you know what you are doing, complex code will be much more difficult under
 * this system. Use IterativeRobot or Command-Based instead if you're new.
 */
public class Robot extends SampleRobot {
	public static RobotDrive myRobot;
    public static RobotDrive shooter;
    public static Victor armBar;
    public static Victor armShooter;
    public static double distance;
	public static AnalogInput distanceSensor; 
    Spark launcher,roller;
    Relay extension;
    CameraServer server, serverTwo;
    double magnitude;
    final double DEADZONE = 0.18;
    final String defaultAuto = "Default";
    final String lowBarAuto = "Low Bar Auto";
    Command getDistance = new GetDistance();
    SendableChooser chooser; //Allows user to choose autonomous command

    public Robot() {
    	myRobot =  new RobotDrive(RobotMap.leftMotorPort,RobotMap.rightMotorPort);
    	shooter = new RobotDrive(RobotMap.shooterPorts[1], RobotMap.shooterPorts[2]);
    	armBar = new Victor(RobotMap.armBarPort);
    	armShooter = new Victor(RobotMap.shooterBarPort);
    	distanceSensor = new AnalogInput(RobotMap.distanceSensorPort);
    	launcher = new Spark(RobotMap.launcherPort);
    	roller = new Spark(RobotMap.rollerPort);
    	extension = new Relay(RobotMap.extensionPort);
    	 server = CameraServer.getInstance();
         server.setQuality(50);
         server.startAutomaticCapture(RobotMap.lifeCam1);
         //serverTwo = CameraServer.getInstance();
         //serverTwo.setQuality(50); 
         //serverTwo.startAutomaticCapture(RobotMap.lifeCam2);
    }
    
    public void robotInit() {
    	getDistance.start();
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", lowBarAuto);
        SmartDashboard.putData("Auto modes", chooser);
    }

	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the if-else structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
    public void autonomous() { //Update Autonomous Code
    	
    	String autoSelected = (String) chooser.getSelected();
		System.out.println("Auto selected: " + autoSelected);
    	
    	switch(autoSelected) {
    	case lowBarAuto:
            myRobot.setSafetyEnabled(false);
            myRobot.drive(-0.5, 1.0);	// spin at half speed
            Timer.delay(2.0);		//    for 2 seconds
            myRobot.drive(0.0, 0.0);	// stop robot
            break;
    	case defaultAuto:
    	default:
            myRobot.setSafetyEnabled(false);
            myRobot.drive(-0.5, 0.0);	// drive forwards half speed
            Timer.delay(2.0);		//    for 2 seconds
            myRobot.drive(0.0, 0.0);	// stop robot
            break;
    	}
    }

    /**
     * Runs the motors with arcade steering.
     */
    public void operatorControl() {
        myRobot.setSafetyEnabled(true);
        while (isOperatorControl() && isEnabled()) {
        	Scheduler.getInstance().run();
        	magnitude = -IO.rightStick.getRawAxis(3) + 1;
        	myRobot.arcadeDrive( magnitude * -IO.rightStick.getY(), magnitude * -IO.rightStick.getZ() ); // drive with arcade style (use right stick)
            //Arm Bar
	        if(IO.controller.getRawAxis(IO.LYAxisNumber) <= DEADZONE && IO.controller.getRawAxis(IO.LYAxisNumber) >= -DEADZONE) {
	        	armBar.set(-0.14);
	        }
	        else {
	        	armBar.set(IO.controller.getRawAxis(IO.LYAxisNumber)/3.0);
	        }
        	//Shooter(Needs to be updated)
	        if(IO.controller.getRawAxis(IO.RYAxisNumber) <= DEADZONE && IO.controller.getRawAxis(IO.RYAxisNumber) >= -DEADZONE ) {
	        	armShooter.set(-0.13);
	        }
	        else if (IO.controller.getRawAxis(IO.RYAxisNumber) > DEADZONE ) { //DOWN
	        	armShooter.set(IO.controller.getRawAxis(IO.RYAxisNumber)/5.0 );
	        }
	        else { //UP
	        	armShooter.set((IO.controller.getRawAxis(IO.RYAxisNumber)/4.0)*2.0);
	        }
        	//Shooter
	        if(IO.controller.getRawAxis(3) > 0.1 )
	        	shooter.arcadeDrive(0,-IO.controller.getRawAxis(IO.rightTriggerNumber)); 
        	//Collector(Opposite of shooter and roller spins)
	        if(IO.controller.getRawAxis(IO.leftTriggerNumber) > 0.1 ) {
	        	shooter.arcadeDrive(0,(IO.controller.getRawAxis(IO.leftTriggerNumber)/5.0)*4.0);
	        	roller.set(0.4);
	        } else {
	        	roller.set(0.0);
	        }
	        if(IO.controller.getRawAxis(IO.leftTriggerNumber) > 0.1 && IO.controller.getRawAxis(IO.rightTriggerNumber) > 0.1 ) {
	        	shooter.arcadeDrive(0,0);
	        	roller.set(0);
	        }
	        //Launcher
	        if (IO.controller.getRawButton(IO.launchButtonNumber)) {
	        	launcher.set(-1);
	        } else {
	        	launcher.set(1);
	        }
	        //Auto Aim
	        if(IO.controller.getRawButton(IO.autoAimButtonNumber)){
	        	try {
					Camera.getCenterValues();
				} catch (NIVisionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	Camera.adjustShooter();
	        }
	        
	        Timer.delay(0.005);		// wait for a motor update time
        }
    }

    /**
     * Runs during test mode
     */
    public void test() {
    }
}
