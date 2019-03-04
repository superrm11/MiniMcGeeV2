package hardware;

import hardware.MotorController;

public class SwerveDrive
{
	private final StepperMotor frontLeftDirection, backLeftDirection, frontRightDirection, backRightDirection;
	private final MotorController frontLeftDrive, backLeftDrive, frontRightDrive, backRightDrive;
	
	private final float deadband = .15f;
	private final int stepsPerRev = 2048;
	
	/**
	 * Creates the Swerve Drive object. This is a class that controls four directional and four drive motors.
	 * This allows it to basically act like a office caster wheel, where the robot can control the direction
	 * that said caster points. This gives it good agility and traction at the same time.
	 * 
	 * @param frontLeftDirection
	 * 			The motor controlling the direction of the front left wheel
	 * @param backLeftDirection
	 * 			The motor controlling the direction of the back left wheel	
	 * @param frontRightDirection
	 * 			The motor controlling the direction of the front right wheel
	 * @param backRightDirection
	 * 			The motor controlling the direction of the back right wheel
	 * @param frontLeftDrive
	 * 			The motor controlling the speed of the front left wheel
	 * @param backLeftDrive
	 * 			The motor controlling the speed of the back left wheel
	 * @param frontRightDrive
	 * 			The motor controlling the speed of the front right wheel
	 * @param backRightDrive
	 * 			The motor controlling the speed of the back right wheel
	 */
	public SwerveDrive(StepperMotor frontLeftDirection, StepperMotor backLeftDirection,
			StepperMotor frontRightDirection, StepperMotor backRightDirection,
			MotorController frontLeftDrive, MotorController backLeftDrive,
			MotorController frontRightDrive, MotorController backRightDrive)
	{
		this.frontLeftDirection = frontLeftDirection;
		this.backLeftDirection = backLeftDirection;
		this.frontRightDirection = frontRightDirection;
		this.backRightDirection = backRightDirection;
		
		this.frontLeftDrive = frontLeftDrive;
		this.backLeftDrive = backLeftDrive;
		this.frontRightDrive = frontRightDrive;
		this.backRightDrive = backRightDrive;
	}
	
	/**
	 * Drives the robot using three axis of joysticks: one for magnitude and direction (polar coordinates),
	 * and one that controls how much the robot will rotate.
	 * @param magnitude
	 * 			Magnitude of the drive joystick, between 0 and 1.0
	 * @param direction
	 * 			Direction of the drive joystick, in degrees, 0 being forward.
	 * @param rotation
	 * 			Rotation, -1 being counter-clockwise, 1 being clockwise.
	 */
	public void drive(double magnitude, double direction, double rotation)
	{
		//If we have any rotation at all, set to 45 and ignore driving commands
		if(Math.abs(rotation) > deadband)
		{
			int steps45 = (int)((stepsPerRev / 360.0) * 45);
			setDirection(steps45, -steps45, -steps45, steps45);
			drive(rotation, rotation, -rotation, -rotation);
		}
		//If we are not rotating, set the direction and drive the robot.
		else if(Math.abs(magnitude) > deadband)
		{
			direction *= (180.0 / Math.PI);
			//Make sure we don't go past the maximum.
			if(direction > 120)
			{
				direction -= 180;
				magnitude *= -1;
			}else if(direction < -120)
			{
				direction += 180;
				magnitude *= -1;
			}
			
			int steps = (int)((stepsPerRev / 360.0) * direction);
			setDirection(steps, steps, steps, steps);
			drive(magnitude, magnitude, magnitude, magnitude);
		}else
		{
			drive(0, 0, 0, 0);
			setDirection(frontLeftDirection.getPosition(), backLeftDirection.getPosition(),
					frontRightDirection.getPosition(), backRightDirection.getPosition());
		}
	}
	
	/**
	 * Sets the speed of all motors.
	 * @param speed
	 * 			How fast the drive motors should go, between -1 and 1
	 */
	private void drive(double frontLeftSpeed, double backLeftSpeed, double frontRightSpeed, double backRightSpeed)
	{
		frontLeftDrive.set(frontLeftSpeed);
		backLeftDrive.set(backLeftSpeed);
		frontRightDrive.set(frontRightSpeed);
		backRightDrive.set(backRightSpeed);
	}
	
	/**
	 * Sets the direction of all stepper motors, relative to their starting point.
	 * @param frontLeftSteps
	 * @param backLeftSteps
	 * @param frontRightSteps
	 * @param backRightSteps
	 */
	private void setDirection(int frontLeftSteps, int backLeftSteps, int frontRightSteps, int backRightSteps)
	{
		frontLeftDirection.setPosition(frontLeftSteps);
		backLeftDirection.setPosition(backLeftSteps);
		frontRightDirection.setPosition(frontRightSteps);
		backRightDirection.setPosition(backRightSteps);
	}
}
