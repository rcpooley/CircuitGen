package com.thirstycircuits.core;

import com.thirstycircuits.display.Resources;

import java.awt.image.BufferedImage;

public class OrGate extends Component
{

	public OrGate()
	{
		super(2, 1);
	}

	@Override
	public void calcOutputs()
	{
		super.calcOutputs();
		outputs[0].setValue(inputs[0].getValue() || inputs[1].getValue());
	}

	@Override
	public BufferedImage getImage()
	{
		return Resources.getImage("or");
	}
}
