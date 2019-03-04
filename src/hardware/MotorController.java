package hardware;

import com.pi4j.wiringpi.SoftPwm;

/**
 * Standard PWM motor controller for the Raspberry Pi.
 *
 * @author Ryan McGee
 */
public class MotorController
{
private boolean isReversed = false;
private int currentValue = 0;

private final int PIN1;
private final int PIN2;

/**
 * Creates the MotorController object and sets the pin modes.
 *
 * @param pin1
 * @param pin2
 */
public MotorController(int pin1, int pin2)
{
	this.PIN1 = pin1;
	this.PIN2 = pin2;
	
	SoftPwm.softPwmCreate(PIN1, 0, 100);
	SoftPwm.softPwmCreate(PIN2, 0, 100);
}

/**
 * Sets the power to the motor, in percentage.
 *
 * @param val -1.0 to 1.0, 0.0 being stopped
 */
public void set(double val)
{
	int scaledVal = (int) (val * 100);
	
	if (this.isReversed)
		scaledVal = -scaledVal;
	
	if (scaledVal < 0)
	{
		SoftPwm.softPwmWrite(PIN1, 0);
		SoftPwm.softPwmWrite(PIN2, -scaledVal);
	} else
	{
		SoftPwm.softPwmWrite(PIN1, scaledVal);
		SoftPwm.softPwmWrite(PIN2, 0);
	}
	this.currentValue = scaledVal;
}

/**
 * Determines if the wheel is reversed. If so, change the parameter rather than
 * the wires.
 *
 * @param reversed
 */
public void setReversed(boolean reversed)
{
	this.isReversed = reversed;
}

/**
 * @return The value being sent via software PWM: between 0 and 100.
 */
public int get()
{
	return currentValue;
}
}
