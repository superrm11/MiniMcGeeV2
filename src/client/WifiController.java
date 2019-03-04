package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A class that controls the robot over a WIFI connection, running in a separate
 * thread.
 *
 * @author Ryan McGee
 */
public class WifiController extends Controller
{

public static final int PORT = 6000;

// ================ SEPERATE THREAD ================

/**
 * Creates the WifiController class, and starts the new thread.
 */
public WifiController()
{
	// Create & start the new thread
	new Thread(new Runnable()
	{
	public void run()
	{
		thread();
	}
	}).start();
}

/**
 * Runs a separate thread from the rest of the code in order to stop it from
 * being blocked.
 */
public void thread()
{
	
	ServerSocket server = null;
	Socket socket = null;
	ObjectInputStream dataIn = null;
	
	try
	{
		// Create the server
		server = new ServerSocket(6000);
		System.out.println("Server Created");
		// Wait for a client to connect
		socket = server.accept();
		System.out.println("Client Connected");
		
		dataIn = new ObjectInputStream(socket.getInputStream());
		System.out.println("Input Stream Created");
		// Loops the thread to continually get data from the client.
		while (endThread == false)
		{
			
			// Get the information
			this.controllerData = (double[]) dataIn.readObject();
			
			// Resets the watch dog
			watchdogLastTime = System.currentTimeMillis();
			
		}
	} catch (IOException | ClassNotFoundException e)
	{
		// If an error occurs, restart the thread and print the error.
		e.printStackTrace();
		try
		{
			dataIn.close();
			socket.close();
			server.close();
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
	} finally
	{
		this.thread();
	}
}

private volatile boolean endThread = false;

/**
 * Ends the thread that receives data from the client.
 */
public void endThread()
{
	this.endThread = true;
}

// ================ END SEPERATE THREAD ================

// Stores all the current data from the client.
private volatile double[] controllerData = new double[18];

private int watchdogTimeout = 200;
private volatile long watchdogLastTime = 0;

/**
 * Gets a device and watches for slow data. If the data is outside the watchdog
 * time, then it will simply return 0.
 *
 * @param device Which device to return
 * @return The device's poll data
 */
private double getDevice(byte device)
{
	if (System.currentTimeMillis() - watchdogLastTime > watchdogTimeout)
		return 0.0;
	if (device >= controllerData.length)
	{
		System.out.println("WARNING! Device requested is OUT of BOUNDS");
		return 0.0;
	}
	return controllerData[device];
}

/**
 * Sets the timeout for controller updates. If it times out, then all values
 * will be set to 0, for safety.
 *
 * @param millis Number of milliseconds before timeout. Default 200.
 */
public void setWatchdogTimeout(int millis)
{
	this.watchdogTimeout = millis;
}

@Override
public double getAxis(byte axis)
{
	return getDevice(axis);
}

@Override
public boolean getButton(byte button)
{
	return (getDevice(button) == 1.0) ? true : false;
}

@Override
public boolean getDPad(DPad button)
{
	switch (button)
	{
	case DOWN:
		return DPAD == .75;
	case UP:
		return DPAD == .25;
	case LEFT:
		return DPAD == 1.0;
	case RIGHT:
		return DPAD == .5;
	default:
		return false;
	}
}

}
