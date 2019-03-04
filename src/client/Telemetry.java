package client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Telemetry
{

ServerSocket telemetryServer;
Socket boundTelemetrySocket;

public Telemetry(int port)
{
	// Creates a separate thread to run the telemetry server, to disable blocking in
	// case nothing answers it.
	(new Thread(() ->
	{
		try
		{
			telemetryServer = new ServerSocket(port);
			boundTelemetrySocket = telemetryServer.accept();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	})).start();
	
}

}
