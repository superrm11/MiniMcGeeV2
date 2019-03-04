package robot;

import com.pi4j.wiringpi.Gpio;

import client.Controller;
import client.XboxController;
import hardware.MotorController;
import hardware.StepperMotor;
import hardware.SwerveDrive;

/**
 * Controls all the functions of the robot: contains the main method.
 *
 * @author Ryan McGee
 */
public class Robot
{
static boolean isUsingWifi = false;
static boolean isUsingBluetooth = false;

static MotorController frontLeft;
static MotorController backLeft;
static MotorController frontRight;
static MotorController backRight;

static StepperMotor frontLeftDirection;
static StepperMotor backLeftDirection;
static StepperMotor frontRightDirection;
static StepperMotor backRightDirection;

static SwerveDrive swerveDrive;

static XboxController controller;

/**
 * The main method of the robot.
 *
 * @param args
 */
public static void main(String[] args)
{
	if (init())
		while (loop())
			;
		
}

/**
 * Initializes all aspects of the robot.
 */
private static boolean init()
{
	if (Gpio.wiringPiSetup() == -1)
	{
		System.out.println("Error setting up WiringPi");
		return false;
	}
	
	// Put init code after this.
	
	controller = new XboxController();
	while(!controller.init())
		;
	
	// Motor controllers
	frontLeft = new MotorController(12, 13);
	backLeft = new MotorController(2, 3);
	frontRight = new MotorController(10, 11);
	backRight = new MotorController(5, 6);
	
	frontLeft.setReversed(true);
	backLeft.setReversed(true);
	frontRight.setReversed(true);
	backRight.setReversed(true);
	
	frontLeftDirection = new StepperMotor(25, 22, 21, 14);
	backLeftDirection = new StepperMotor(0, 7, 9, 8);
	frontRightDirection = new StepperMotor(29, 28, 27, 26);
	backRightDirection = new StepperMotor(4, 1, 16, 15);
	
	StepperMotor.initialize();
	// end motor controllers
	
	// Servos
	
	// Transmission
	swerveDrive = new SwerveDrive(frontLeftDirection, backLeftDirection, frontRightDirection, backRightDirection,
			frontLeft, backLeft, frontRight, backRight);
	// Reverse motors
	return true;
}

/**
 * The main loop that runs after the initialization.
 *
 * @return whether or not to continue the loop.
 */
private static boolean loop()
{
	// ========LOOP========
	
	// ========DRIVING FUNCTIONS========
	
	swerveDrive.drive(controller.getMagnitude(Controller.LSTICK), controller.getDirection(Controller.LSTICK),
			controller.getAxis(Controller.RX_AXIS));
	
	// ========END DRIVING FUNCTIONS========
	
	// ========DEBUG========
	
	printStatements();
	
	// ========END DEBUG========
	
	// Limit the thread to update only every 25 milliseconds.
	try
	{
		Thread.sleep(25);
	} catch (InterruptedException e)
	{
		e.printStackTrace();
	}
	
	// =======END LOOP========
	return true;
}

/**
 * Used for debug purposes.
 */
private static void printStatements()
{
	
}
}
