package hardware;

import java.util.ArrayList;

import com.pi4j.wiringpi.Gpio;

public class StepperMotor
{

//==================================STEPPER THREADING=================================

private static ArrayList<StepperMotor> stepperList = new ArrayList<StepperMotor>();
private static Thread stepperThread = new Thread(new Runnable()
{
@Override
public void run()
{
	while(!disableThread)
	{
		if(pauseThread)
			break;
		
		for(StepperMotor motor : stepperList)
			motor.update();
	}
}

});

private static boolean disableThread = false, pauseThread = false;

/**
 * Initializes all stepper motors by starting the stepper motor thread.
 * This is a thread that controls the movements of all stepper motors
 * that have been created.
 */
public static void initialize()
{
	stepperThread.start();
}
/**
 * Signals to end the stepper thread. This cannot be restarted.
 */
public static void disableThread()
{
	disableThread = true;
}

/**
 * Momentarily pauses the stepper motor thread, saving current states of all wheels.
 */
public static void pauseThread()
{
	pauseThread = true;
}

/**
 * Resumes the thread after a pause, continuing right where it left off.
 */
public static void resumeThread()
{
	pauseThread = false;
}


//===========================END STEPPER THREADING===============================

//===========================STEPPER OBJECT======================================


public enum HoldMode
{
	POWERED, UNPOWERED
}

private final int PIN1, PIN2, PIN3, PIN4;

private volatile HoldMode holdMode = HoldMode.UNPOWERED;
private volatile int lowerLimit = 0, upperLimit = 0;
private volatile int currentStep = 0, position = 0;
private volatile int requestedPosition = 0;
private volatile int millisDelay = 5;
private volatile long timer = 0;

public StepperMotor(int pin1, int pin2, int pin3, int pin4)
{
	this.PIN1 = pin1;
	this.PIN2 = pin2;
	this.PIN3 = pin3;
	this.PIN4 = pin4;
	
	Gpio.pinMode(pin1, Gpio.OUTPUT);
	Gpio.pinMode(pin2, Gpio.OUTPUT);
	Gpio.pinMode(pin3, Gpio.OUTPUT);
	Gpio.pinMode(pin4, Gpio.OUTPUT);
	
	stepperList.add(this);
}

/**
 * Sets the limit for where the stepper motor is able to operate. If both values are
 * the same, then there is no boundary.
 * 
 * @param lowerBound
 * 			The least number of steps allowed, in the counter-clockwise direction.
 * @param upperBound
 * 			The most number of steps allowed, in the clockwise direction.
 */
public void limitPoisition(int lowerBound, int upperBound)
{
	lowerLimit = lowerBound;
	upperLimit = upperBound;
}

/**
 * Sets the absolute position of the stepper motor. If the current step is above the 
 * input steps, it will move downwards, and vice versa for below.
 * @param steps the absolute position to go to
 * @return
 * 		The distance away from the goal, in steps.
 */
public int setPosition(int steps)
{
	requestedPosition = steps;
	return requestedPosition - position;
}

/**
 * Moves x amount of steps based on it's current position, setting the position 
 * "relatively" rather than absolute. 
 * @param steps
 * 			positive for clockwise rotation, negative for counter-clockwise.
 * @return
 * 		The number of steps away from the goal
 */
public int movePosition(int steps)
{
	if(upperLimit != lowerLimit && (steps + position > upperLimit || steps + position < lowerLimit))
		return 0;
	requestedPosition = steps + position;
	return requestedPosition - position;
}

/**
 * Effectively turns off power to the stepper motor. This makes it good for 
 * power and heat management, but makes it easier to move via outside
 * forces.
 */
public void cutPower()
{
	Gpio.digitalWrite(PIN1, false);
	Gpio.digitalWrite(PIN2, false);
	Gpio.digitalWrite(PIN3, false);
	Gpio.digitalWrite(PIN4, false);
}

/**
 * Sets how the motor responds to no movement / commands. If it is in POWERED
 * mode, then it will retain the last command sent, holding the motor in that position.
 * If it's in UNPOWERED mode, then it will cut power to the motor whenever it stops receiving
 * commands, allowing it to save power and cut down on heat.
 * @param mode
 * 			POWERED or UNPOWERED
 */
public void setHoldMode(HoldMode mode)
{
	holdMode = mode;
}

/**
 * Sets the minimum amount of time between commands sent to the motor. This effectively controls
 * the max speed of the motor. The lower the number, the faster (and less accurate due to step skips)
 * it gets, and the lower the number, the slower (and more stable) it gets.
 * @param millis
 * 			Time delay between commands, in milliseconds.
 */
public void setMinDelay(int millis)
{
	millisDelay = millis;
}

/**
 * @return
 * 		the current step that the motor is active on, in relation to where it was zeroed.
 */
public int getPosition()
{
	return position;
}

/**
 * Resets the absolute position of the stepper motor back to zero. Use for calibration.
 */
public void setZero()
{
	position = 0;
}

/**
 * Moves the motor one step either in the positive (clockwise) direction, or
 * the negative (counter-clockwise) direction.
 * @param direction
 * 			Positive for clockwise, negative for counter-clockwise
 */
public int step(int direction)
{ 		
	//Keeps the stepper motor between the upper and lower limit by not running
	//if it's attempting to go past it.
	if(lowerLimit != upperLimit && position > upperLimit && direction > 0)
		return position;
	else if(lowerLimit != upperLimit && position < lowerLimit && direction < 0)
		return position;
	
	
	switch(currentStep)
	{
	case 0:
		Gpio.digitalWrite(PIN1, true);
		Gpio.digitalWrite(PIN2, true);
		Gpio.digitalWrite(PIN3, false);
		Gpio.digitalWrite(PIN4, false);
	break;
	case 1:
		Gpio.digitalWrite(PIN1, false);
		Gpio.digitalWrite(PIN2, true);
		Gpio.digitalWrite(PIN3, true);
		Gpio.digitalWrite(PIN4, false);
	break;
	case 2:
		Gpio.digitalWrite(PIN1, false);
		Gpio.digitalWrite(PIN2, false);
		Gpio.digitalWrite(PIN3, true);
		Gpio.digitalWrite(PIN4, true);
	break;
	case 3:
		Gpio.digitalWrite(PIN1, true);
		Gpio.digitalWrite(PIN2, false);
		Gpio.digitalWrite(PIN3, false);
		Gpio.digitalWrite(PIN4, true);
	break;
	}
	
	//Advances the step for the next iteration of step()
	currentStep += (direction > 0 ? 1 : -1);	
	
	if(currentStep >= 4)
		currentStep = 0;
	else if(currentStep <= -1)
		currentStep = 3;
	
	//Keeps the absolute position updated.
	position += (direction > 0 ? 1 : -1);
	
	return position;
}

/**
 * Updates the stepper motor in terms of position.
 */
void update()
{
	//Keeps the stepper clock updated, makes sure that steps only occur after the minimum delay.
	if(System.currentTimeMillis() - timer < millisDelay)
		return;
	else
		timer = System.currentTimeMillis();

	if(requestedPosition != position)
	{
		step(requestedPosition > position ? 1 : -1);
	}else if(holdMode == HoldMode.UNPOWERED)
	{
		cutPower();
	}
}
}
