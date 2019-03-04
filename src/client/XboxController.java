package client;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * The class that controls getting information from the Xbox Controller.
 *
 * @author ryan
 *
 */
public class XboxController extends client.Controller
{

private Controller[] controllerList;
private Component[] componentList;

private Controller controller;

/**
 * Initializes the controller: looks for it, and assigns the class object. This
 * is best run in a loop.
 *
 * @return Whether or not the initialization failed.
 */
public boolean init()
{
	// Gets the system joysticks
	this.controllerList = ControllerEnvironment.getDefaultEnvironment().getControllers();
	if (controllerList.length < 2)
		return false;
	
	// Only look for the first joystick that's name contains "xbox".
	for (Controller c : controllerList)
	{
		if (c.getName().toLowerCase().contains("xbox") && c.getComponents().length == 18)
		{
			this.controller = c;
			componentList = c.getComponents();
			return true;
		}
	}
	return false;
}

/**
 * Gets the Directional Pad buttons. It's not a traditional button, so use this
 * instead.
 *
 * @param button The button direction
 * @return Whether or not the button is pressed
 */
public boolean getDPad(DPad button)
{
	switch (button)
	{
	case LEFT:
		return componentList[DPAD].getPollData() == 1.0;
	case RIGHT:
		return componentList[DPAD].getPollData() == 0.5;
	case UP:
		return componentList[DPAD].getPollData() == 0.25;
	case DOWN:
		return componentList[DPAD].getPollData() == 0.75;
	default:
		break;
	}
	return false;
	
}

/**
 * Gets the button specified.
 *
 * @param button Which button will be tested
 * @return Whether that button is pressed
 */
public boolean getButton(byte button)
{
	update();
	return componentList[button].getPollData() == 1.0;
}

/**
 * Gets the value from the axis specified: from -1 to 1 with 0 being "centered".
 *
 * @param axis The axis that will be retrieved
 * @return the value, in decimal percentage, that the button is at.
 */
public double getAxis(byte axis)
{
	update();
	return componentList[axis].getPollData();
}

/**
 * Polls each component for updated values. Buttons / axis will not register
 * unless this is run before it.
 */
private void update()
{
	this.controller.poll();
}

}
