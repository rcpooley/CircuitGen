package com.thirstycircuits.display;

import com.thirstycircuits.core.Component;
import com.thirstycircuits.core.NotGate;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class LocComponent
{

	private Point location;
	private Dimension size;
	private Component component;
	private int rotation;

	public LocComponent(Point location, Dimension size, int rotation, Component component)
	{
		this.location = location;
		this.size = size;
		this.component = component;
		this.rotation = rotation;
	}

	public Point getLocation()
	{
		return location;
	}

	public Dimension getSize()
	{
		return size;
	}

	public int getRotation()
	{
		return rotation;
	}

	public void setRotation(int rot)
	{
		this.rotation = rot;
	}

	public Component getComponent()
	{
		return component;
	}

	public void setComponent(Component component)
	{
		this.component = component;
	}

	public boolean isInput(int nodeIndex)
	{
		return nodeIndex < component.getInputs().length;
	}

	public List<Double[]> getDots()
	{
		List<Double[]> dots = new ArrayList<>();

		int ni = component.getInputs().length;

		if (rotation % 2 == 0)
		{
			double x = rotation == 0 ? 0 : 1;
			if (ni == 2)
			{
				dots.add(new Double[] {x, 0.25});
				dots.add(new Double[] {x, 0.75});
			}
			else
			{
				dots.add(new Double[] {x, 0.5});
			}
			dots.add(new Double[] {1 - x, 0.5});
		}
		else
		{
			double y = rotation == 1 ? 0 : 1;
			if (ni == 2)
			{
				dots.add(new Double[] {0.25, y});
				dots.add(new Double[] {0.75, y});
			}
			else
			{
				dots.add(new Double[] {0.5, y});
			}
			dots.add(new Double[] {0.5, 1 - y});
		}

		return dots;
	}
}
