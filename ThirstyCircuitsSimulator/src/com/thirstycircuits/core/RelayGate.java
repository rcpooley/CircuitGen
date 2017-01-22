package com.thirstycircuits.core;

import com.thirstycircuits.display.Resources;

import java.awt.image.BufferedImage;

public class RelayGate extends Component
{
	public RelayGate()
	{
		super(1, 1);
	}

	@Override
	public void calcOutputs()
	{
		super.calcOutputs();
		outputs[0].setValue(inputs[0].getValue());
	}

	@Override
	public BufferedImage getImage()
	{
		return Resources.getImage("relay");
	}
}
