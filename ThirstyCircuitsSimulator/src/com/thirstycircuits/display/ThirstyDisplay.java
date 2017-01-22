package com.thirstycircuits.display;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ThirstyDisplay
{

	private JFrame frame;
	private JLabel label;

	public ThirstyDisplay()
	{
		frame = new JFrame("Thirsty Circuit Display");
		frame.setSize(800,600);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		label = new JLabel();
		frame.add(label);
		frame.setVisible(true);
	}

	public JFrame getFrame()
	{
		return frame;
	}

	public BufferedImage getCanvas()
	{
		int wid = Math.max(label.getWidth(), 1);
		int hei = Math.max(label.getHeight(), 1);
		return new BufferedImage(wid, hei, BufferedImage.TYPE_INT_ARGB);
	}

	public void update(BufferedImage img)
	{
		label.setIcon(new ImageIcon(img));
	}

	public Point getOffset()
	{
		int xoff = (frame.getWidth() - label.getWidth()) / 2;
		int yoff = frame.getHeight() - label.getHeight() - xoff;
		return new Point(xoff, yoff);
	}
}
