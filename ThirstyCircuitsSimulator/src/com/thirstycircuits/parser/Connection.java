package com.thirstycircuits.parser;

public class Connection
{

	private int fromId, toId, toNode;

	public Connection(int fromId, int toId, int toNode)
	{
		this.fromId = fromId;
		this.toId = toId;
		this.toNode = toNode;
	}

	public int getFromId()
	{
		return fromId;
	}

	public int getToId()
	{
		return toId;
	}

	public int getToNode()
	{
		return toNode;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Connection)) return false;
		Connection c = (Connection) obj;
		return c.fromId == fromId && c.toId == toId && c.toNode == toNode;
	}
}
