package com.thirstycircuits.display;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Resources
{

	private static final Map<String, BufferedImage> images = new HashMap<>();

	public static void init()
	{
		String[] load = {"and", "or", "not", "relay", "po"};
		for (String s : load)
		{
			try
			{
				images.put(s, ImageIO.read(Resources.class.getResourceAsStream("/res/" + s + ".png")));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static BufferedImage getImage(String s)
	{
		return images.get(s);
	}

	public static BufferedImage rotateImage(BufferedImage img, int rot)
	{
		BufferedImage out;
		switch(rot)
		{
			case 0:
				out = img;
				break;
			case 1:
				out = new BufferedImage(img.getHeight(), img.getWidth(), img.getType());
				for (int x = 0; x < img.getWidth(); x++)
				{
					for (int y = 0; y < img.getHeight(); y++)
					{
						out.setRGB(img.getHeight() - y - 1, x, img.getRGB(x, y));
					}
				}
				break;
			case 2:
				out = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
				for (int x = 0; x < img.getWidth(); x++)
				{
					for (int y = 0; y < img.getHeight(); y++)
					{
						out.setRGB(img.getWidth() - x - 1, img.getHeight() - y - 1, img.getRGB(x, y));
					}
				}
				break;
			default:
				out = new BufferedImage(img.getHeight(), img.getWidth(), img.getType());
				for (int x = 0; x < img.getWidth(); x++)
				{
					for (int y = 0; y < img.getHeight(); y++)
					{
						out.setRGB(y, img.getWidth() - x - 1, img.getRGB(x, y));
					}
				}
				break;
		}
		return out;
	}
}
