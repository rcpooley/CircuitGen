package com.thirstycircuits.network;

import com.thirstycircuits.display.Resources;
import com.thirstycircuits.parser.Parser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkMain
{

	public static void main(String[] args)
	{
		Parser.init();
		int port = 7276;
		if (args.length > 0)
		{
			try
			{
				port = Integer.parseInt(args[0]);
			}
			catch (Exception e)
			{
				System.err.println("Invalid integer, defaulting to port " + port);
			}
		}

		try
		{
			ServerSocket server = new ServerSocket(port);
			System.out.println("Server started on port " + port);
			while (true)
			{
				Socket socket = server.accept();
				new SocketHandler(socket);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
