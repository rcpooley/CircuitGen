package com.thirstycircuits.core;

import java.util.ArrayList;
import java.util.List;

public class Value
{

	private boolean value;
	private List<Component> updateOnChange;

	public Value()
	{
		updateOnChange = new ArrayList<>();
	}

	public boolean getValue()
	{
		return value;
	}

	public void setValue(boolean value)
	{
		this.value = value;
		updateOnChange.forEach(Component::calcOutputs);
	}

	public List<Component> getUpdateOnChange()
	{
		return updateOnChange;
	}
}
