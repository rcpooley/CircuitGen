package com.thirstycircuits.core;

import com.thirstycircuits.parser.Connection;
import com.thirstycircuits.parser.Parser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class Circuit extends Component
{

	private Component[] components;

	public Circuit(List<Value> inputs, List<Value> outputs, Component[] components)
	{
		super(inputs.size(), outputs.size());
		for (int i = 0; i < inputs.size(); i++)
		{
			this.inputs[i] = inputs.get(i);
		}
		for (int i = 0; i < outputs.size(); i++)
		{
			this.outputs[i] = outputs.get(i);
		}
		this.components = components;
	}

	public Circuit(Value[] inputs, Value[] outputs, Component[] components)
	{
		super(inputs.length, outputs.length);
		this.inputs = inputs;
		this.outputs = outputs;
		this.components = components;
	}

	@Override
	public void calcOutputs() {}

	@Override
	public BufferedImage getImage()
	{
		return null;
	}

	public List<Component> getComponents()
	{
		List<Component> components = new ArrayList<>();

		for (Component c : this.components)
			components.add(c);

		return components;
	}

	public void resetHitCounters()
	{
		for (Component c : components)
		{
			c.resetHitCounter();
		}
	}

	public String toJson()
	{
		List<Component> components = getComponents();

		List<Connection> connections = new ArrayList<>();

		for (int toId = 0; toId < components.size(); toId++)
		{
			Component to = components.get(toId);
			for (int i = 0; i < to.getInputs().length; i++)
			{
				Value v = to.getInputs()[i];
				boolean atLeastOne = false;
				for (int frmId = 0; frmId < components.size(); frmId++)
				{
					Component from = components.get(frmId);
					if (from.getOutputs()[0] == v)
					{
						Connection c = new Connection(frmId, toId, i);
						if (!connections.contains(c))
							connections.add(c);
						atLeastOne = true;
					}
				}

				//If this input doesn't connect to anything, add it as an input value
				if (!atLeastOne)
				{
					Connection c = new Connection(-1, toId, i);
					if (!connections.contains(c))
						connections.add(c);
				}
			}

			//check for output values
			for (int i = 0; i < to.getOutputs().length; i++)
			{
				Value v = to.getOutputs()[i];
				if (v.getUpdateOnChange().size() == 0)
				{
					Connection c = new Connection(toId, -1, 0);
					if (!connections.contains(c))
						connections.add(c);
				}
			}
		}

		JSONArray comps = new JSONArray();
		for (Component c : components)
		{
			comps.put(Parser.getComponentId(c.getClass()));
		}

		JSONArray conns = new JSONArray();
		for (Connection i : connections)
		{
			JSONObject conn = new JSONObject();
			conn.put("fromid", i.getFromId());
			conn.put("toid", i.getToId());
			conn.put("tonode", i.getToNode());
			conns.put(conn);
		}

		JSONObject out = new JSONObject();
		out.put("components", comps);
		out.put("connections", conns);
		return out.toString();
	}
}
