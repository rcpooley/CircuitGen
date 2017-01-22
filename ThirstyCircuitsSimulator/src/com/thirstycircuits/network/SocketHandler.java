package com.thirstycircuits.network;

import com.thirstycircuits.parser.Parser;
import com.thirstycircuits.parser.Truths;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class SocketHandler
{

	private static final List<SocketHandler> handlers = new ArrayList<>();

	private Socket socket;
	private volatile boolean running;
	private String readString;

	public SocketHandler(Socket socket)
	{
		handlers.add(this);
		System.out.println("AD: " + handlers.size());
		this.socket = socket;
		this.readString = "";

		new Thread("Socket Thread") {
			@Override
			public void run()
			{
				running = true;
				readLoop();
			}
		}.start();
	}

	private void readLoop()
	{
		while (running)
		{
			try
			{
				InputStream in = socket.getInputStream();
				socket.setSoTimeout(1000);
				byte[] buffer = new byte[1024];
				int read;
				while ((read = in.read(buffer)) > 0)
				{
					byte[] b = new byte[read];
					for (int i = 0; i < read; i++)
						b[i] = buffer[i];
					readString += new String(b, "UTF-8");
				}
				if (read == -1)
				{
					running = false;
				}
			}
			catch (SocketTimeoutException e)
			{
				System.out.println("HANDLING " + readString);
				String toSendBack;
				try
				{
					Truths truth = Parser.calcTruthTable(Parser.parse(readString));
					toSendBack = truth.toJson();
				}
				catch (Exception ee)
				{
					ee.printStackTrace();
					toSendBack = new JSONObject().put("success", false).toString();
				}

				try
				{
					socket.getOutputStream().write(toSendBack.getBytes());
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
				running = false;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				running = false;
			}
		}
		handlers.remove(this);
		System.out.println("RM: " + handlers.size());
	}
}
