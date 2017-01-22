package com.thirstycircuits.core;

import java.awt.image.BufferedImage;

public abstract class Component
{

	private static final int MAX_HIT_COUNTER = 100;

	protected Value[] inputs, outputs;
	private int hitCounter;

	public Component(int numInputs, int numOutputs)
	{
		inputs = new Value[numInputs];
		for (int i = 0; i < numInputs; i++)
		{
			setInput(i, new Value());
		}
		outputs = new Value[numOutputs];
		for (int i = 0; i < numOutputs; i++)
		{
			outputs[i] = new Value();
		}
	}

	public void setInput(int ind, Value value)
	{
		inputs[ind] = value;
		value.getUpdateOnChange().add(this);
	}

	public Value getOutput(int ind)
	{
		return outputs[ind];
	}

	public Value[] getInputs()
	{
		return inputs;
	}

	public Value[] getOutputs()
	{
		return outputs;
	}

	public void calcOutputs()
	{
		hitCounter++;
		if (hitCounter >= MAX_HIT_COUNTER)
		{
			throw new ComponentException("Component was updated too many times");
		}
	}

	public abstract BufferedImage getImage();

	public void resetHitCounter()
	{
		hitCounter = 0;
	}
}
