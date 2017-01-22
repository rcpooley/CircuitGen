package com.circuits.android.circuits;

import org.json.JSONObject;

public class Truth
{

	private boolean[][] table;
	private int numInputs;

	public Truth(boolean[][] table, int numInputs, String[] inputIds)
	{
		this.table = table;
		this.numInputs = numInputs;
	}

	@Override
	public String toString()
	{
		String build = "";
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

	public boolean[][] getTable() {
		return table;
	}

	public int getNumInputs() {
		return numInputs;
	}
}
