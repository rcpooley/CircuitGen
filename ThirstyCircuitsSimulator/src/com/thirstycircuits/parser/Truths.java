package com.thirstycircuits.parser;

import org.json.JSONObject;

public class Truths
{

	private boolean[][] table;
	private int numInputs;
	private String[] inputIds;

	public Truths(boolean[][] table, int numInputs, String[] inputIds)
	{
		this.table = table;
		this.numInputs = numInputs;
		this.inputIds = inputIds;
	}

	public String toJson()
	{
		JSONObject obj = new JSONObject();
		obj.put("success", true);
		obj.put("table", table);
		return obj.toString();
	}

	@Override
	public String toString()
	{
		String build = "";
		for (String inp : inputIds)
		{
			String s = inp;
			while (s.length() < 4) s = " " + s;
			build += s + ", ";
		}
		build += "\n";
		for (int y = 0; y < table[0].length; y++)
		{
			String b = "";
			for (int i = 0; i < numInputs; i++)
			{
				if (i > 0) b += ", ";
				b += table[i][y];
			}
			b += " = ";
			for (int i = numInputs; i < table.length; i++)
			{
				if (i > numInputs) b += ", ";
				b += table[i][y];
			}
			build += b + "\n";
		}
		return build;
	}
}
