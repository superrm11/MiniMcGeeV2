package hardware;

import com.pi4j.wiringpi.SoftPwm;

/**
 * Controls a servo using software PWM on the Raspberry Pi.
 *
 * @author Ryan McGee
 */
public class Servo
{

	private final int pin;
	private final int min, max;
	private final double percentPerSecond;

	private int currentPosition;

	/**
	 * Creates the servo object.
	 *
	 * @param pin          which DIO port the servo is on
	 * @param startingPos  where the servo should start
	 * @param minDegrees   the minimum position
	 * @param maxDegrees   the maximum position
	 * @param totalDegrees total number of degrees the servo turns, for scaling.
	 * @param speed        how fast the servo should turn, in degrees per second.
	 */
	public Servo(int pin, int startingPos, int minDegrees, int maxDegrees, int totalDegrees, double speed)
	{
		this.pin = pin;
		this.currentPosition = startingPos;
		this.min = minDegrees;
		this.max = maxDegrees;
		this.percentPerSecond = speed;
		SoftPwm.softPwmCreate(pin, startingPos, totalDegrees);
	}

	/**
	 * Sets the position of the servo as long as it's between the min and max.
	 *
	 * @param value the value to set to the servo
	 */
	public void setPosition(byte value)
	{
		if (value < max && value > min)
			SoftPwm.softPwmWrite(pin, value);
	}

	/**
	 * Changes the position of the servo based on the percentage given and the speed
	 * defined in the class.
	 *
	 * @param percentage percent of maximum speed defined in the class.
	 */
	public void changePosition(double percentage)
	{
		// IF the state is disabled (0), reset time and do nothing.
		if (Math.abs(percentage) < .1)
		{
			this.lastPositionTime = System.currentTimeMillis();
			return;
		}

		// else, increase / decrease servo position based on speed given.
		long timeChange = System.currentTimeMillis() - lastPositionTime;
		int positionChange = (int) (this.percentPerSecond * (timeChange / 1000.0));

		if (positionChange == 0)
			return;

		if (currentPosition + (percentage * positionChange) < max
				&& currentPosition + (percentage * positionChange) > min)
			this.currentPosition += (percentage * positionChange);

		SoftPwm.softPwmWrite(this.pin, currentPosition);
		System.out.println("position: " + currentPosition);

		this.lastPositionTime = System.currentTimeMillis();
	}

	private long lastPositionTime = 0;

}
