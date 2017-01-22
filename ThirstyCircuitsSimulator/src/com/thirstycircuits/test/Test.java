package com.thirstycircuits.test;

import com.thirstycircuits.core.Circuit;
import com.thirstycircuits.parser.Parser;
import com.thirstycircuits.parser.Truths;

public class Test
{

	public static void main(String[] args)
	{
		Parser.init();
		Circuit circuit = Parser.parse("{\"connections\": [{\"toid\": 0, \"fromid\": -1, \"tonode\": 0},{\"toid\": 1, \"fromid\": -2, \"tonode\": 0}, {\"toid\": 1, \"fromid\": -1, \"tonode\": 1},  {\"toid\": 2, \"fromid\": 1, \"tonode\": 1}, {\"toid\": 3, \"fromid\": 0, \"tonode\": 1}, {\"toid\": 3, \"fromid\": -3, \"tonode\": 0},{\"toid\": 2, \"fromid\": 3, \"tonode\": 0,{\"toid\": -1, \"fromid\": 2, \"tonode\": -1}], \"components\": [2, 0, 1, 1]}");
		Truths truth = Parser.calcTruthTable(circuit);
		System.out.println(truth);
	}

}
