package com.thirstycircuits.parser;

import com.thirstycircuits.core.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser
{

	private static final Map<Integer, Class<? extends Component>> componentClasses = new HashMap<>();

	public static void init()
	{
		componentClasses.put(0, AndGate.class);
		componentClasses.put(1, OrGate.class);
		componentClasses.put(2, NotGate.class);
		componentClasses.put(3, RelayGate.class);
	}

	public static int getComponentId(Class<? extends Component> clazz)
	{
		for (int id : componentClasses.keySet())
		{
			if (componentClasses.get(id) == clazz) return id;
		}
		return -1;
	}

	public static Circuit parse(String json)
	{
		JSONObject obj = new JSONObject(json);

		JSONArray comps = obj.getJSONArray("components");
		JSONArray conns = obj.getJSONArray("connections");

		Component[] components = new Component[comps.length()];
		for (int i = 0; i < comps.length(); i++)
		{
			int id = comps.getInt(i);
			if (!componentClasses.containsKey(id))
			{
				throw new RuntimeException("Unrecognized component id: " + id);
			}
			try
			{
				components[i] = componentClasses.get(id).newInstance();
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		List<Value> inputs = new ArrayList<>(), outputs = new ArrayList<>();

		Map<Integer, Value> cachedInputs = new HashMap<>();

		for (int i = 0; i < conns.length(); i++)
		{
			JSONObject conn = conns.getJSONObject(i);
			int fromId = conn.getInt("fromid");
			int toId = conn.getInt("toid");
			int toNode = conn.getInt("tonode");
			if (fromId != -1 || toId != -1 || toNode != -1)
			{
				if (fromId < 0)
				{
					Value v;
					if (cachedInputs.containsKey(fromId))
					{
						v = cachedInputs.get(fromId);
					}
					else
					{
						v = new Value();
						cachedInputs.put(fromId, v);
						inputs.add(v);
					}
					components[toId].setInput(toNode, v);
				}
				else if (toId == -1)
				{
					outputs.add(components[fromId].getOutput(0));
				}
				else
				{
					Value inp = components[fromId].getOutput(0);
					components[toId].setInput(toNode, inp);
				}
			}
		}

		return new Circuit(inputs, outputs, components);
	}

	public static Truths calcTruthTable(Circuit circuit)
	{
		try
		{
			circuit.resetHitCounter();
			boolean[][] table = new boolean[circuit.getInputs().length + circuit.getOutputs().length][2 << (circuit.getInputs().length - 1)];
			for (int i = 0; i < table[0].length; i++)
			{
				//Set inputs
				for (int j = 0; j < circuit.getInputs().length; j++)
				{
					table[j][i] = isSet(i, circuit.getInputs().length - j - 1);
					circuit.getInputs()[j].setValue(table[j][i]);
				}

				//Now set outputs
				for (int j = 0; j < circuit.getOutputs().length; j++)
				{
					table[circuit.getInputs().length + j][i] = circuit.getOutputs()[j].getValue();
				}
			}

			//build inputIds
			String[] inputIds = new String[circuit.getInputs().length];
			List<Component> comps = circuit.getComponents();
			for (int i = 0; i < circuit.getInputs().length; i++)
			{
				Value v = circuit.getInputs()[i];
				Component c = v.getUpdateOnChange().get(0);
				int inputNode = -1;
				for (int j = 0; j < c.getInputs().length; j++)
				{
					if (c.getInputs()[j] == v) inputNode = j;
				}
				inputIds[i] = comps.indexOf(c) + "." + inputNode;
			}
			return new Truths(table, circuit.getInputs().length, inputIds);
		}
		catch (ComponentException e)
		{
			System.err.println(e.getMessage());
			return null;
		}
	}

	private static boolean isSet(int bits, int index)
	{
		return (bits & (1 << index)) > 0 && index < 32;
	}
}
