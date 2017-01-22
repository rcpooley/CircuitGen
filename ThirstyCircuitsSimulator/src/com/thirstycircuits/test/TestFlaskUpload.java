package com.thirstycircuits.test;

import com.thirstycircuits.display.Resources;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.security.cert.CRL;

public class TestFlaskUpload
{

	public static void main(String[] args) throws IOException
	{
		Resources.init();
		uploadImage(TestFlaskUpload.class.getResourceAsStream("/res/cirrr.png"));
	}

	public static void uploadImage(InputStream stream) throws IOException
	{
		String url = "http://54.213.237.53:5000/upload";
		//url = "http://localhost:5000/upload";
		String charset = "UTF-8";
		String boundary = Long.toHexString(System.currentTimeMillis());
		String CRLF = "\r\n";

		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

		try (
				OutputStream output = connection.getOutputStream();
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
		)
		{
			writer.append("--" + boundary).append(CRLF);
			writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"image.png\"").append(CRLF);
			writer.append("Content-Type: multipart/form-data").append(CRLF);
			writer.append("Content-Transfer-Encoding: binary").append(CRLF);
			writer.append(CRLF).flush();

			int read = 0;
			byte[] buffer = new byte[1024];
			while ((read = stream.read(buffer)) > 0)
			{
				output.write(buffer, 0, read);
			}

			output.flush();
			writer.append(CRLF).flush();
			writer.append("--" + boundary + "--").append(CRLF).flush();
		}

		int code = connection.getResponseCode();
		System.out.println(code);

		InputStream input;
		if (code == 200)
			input = connection.getInputStream();
		else
			input = connection.getErrorStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String line;
		while ((line = reader.readLine()) != null)
		{
			System.out.println(line);
		}
		reader.close();
	}
}
