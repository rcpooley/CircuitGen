package com.thirstycircuits.display;

import com.thirstycircuits.core.*;
import com.thirstycircuits.core.Component;
import com.thirstycircuits.parser.Connection;
import com.thirstycircuits.parser.Parser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CircuitDrawer implements MouseListener, MouseMotionListener, KeyListener
{

	private static class Conn
	{
		public LocComponent from, to;
		public int toNode;

		public Conn(LocComponent from, LocComponent to, int toNode)
		{
			this.from = from;
			this.to = to;
			this.toNode = toNode;
		}
	}

	public static void main (String[] args)
	{
		Resources.init();
		Parser.init();
		new CircuitDrawer();
	}

	private ThirstyDisplay display;
	private List<LocComponent> components;
	private List<Conn> connections;
	private double pixelRatio;
	private int gridMode = 1;
	private volatile boolean running;

	private Point mse = new Point(0, 0);
	private LocComponent tempComponent, hoverComponent;
	private int[] tempNode, hoverNode;
	private boolean wiringMode;

	public CircuitDrawer()
	{
		display = new ThirstyDisplay();
		display.getFrame().addMouseListener(this);
		display.getFrame().addMouseMotionListener(this);
		display.getFrame().addKeyListener(this);
		components = Collections.synchronizedList(new ArrayList<>());
		connections = Collections.synchronizedList(new ArrayList<>());
		pixelRatio = 10;

		new Thread("Drawing Thread") {
			@Override
			public void run()
			{
				running = true;
				drawLoop();
			}
		}.start();
	}

	public Circuit toCircuit()
	{
		JSONArray comps = new JSONArray();
		JSONArray conns = new JSONArray();
		synchronized (components)
		{
			for (int j = 0; j < components.size(); j++)
			{
				LocComponent c = components.get(j);
				comps.put(Parser.getComponentId(c.getComponent().getClass()));
				for (int i = 0; i < c.getComponent().getInputs().length; i++)
				{
					if (!connToInput(c, i))
					{
						JSONObject obj = new JSONObject();
						obj.put("fromid", -1);
						obj.put("toid", j);
						obj.put("tonode", i);
						conns.put(obj);
					}
				}

				if (!connToOutput(c))
				{
					JSONObject obj = new JSONObject();
					obj.put("fromid", j);
					obj.put("toid", -1);
					obj.put("tonode", 0);
					conns.put(obj);
				}
			}
		}

		synchronized (connections)
		{
			for (Conn c : connections)
			{
				int fromId = components.indexOf(c.from);
				int toId = components.indexOf(c.to);
				JSONObject obj = new JSONObject();
				obj.put("fromid", fromId);
				obj.put("toid", toId);
				obj.put("tonode", c.toNode);
				conns.put(obj);
			}
		}

		JSONObject obj = new JSONObject();
		obj.put("components", comps);
		obj.put("connections", conns);
		return Parser.parse(obj.toString());
	}

	private boolean connToInput(LocComponent comp, int inputNode)
	{
		synchronized (connections)
		{
			for (Conn c : connections)
			{
				if (c.to == comp && c.toNode == inputNode) return true;
			}
		}
		return false;
	}

	private boolean connToOutput(LocComponent comp)
	{
		synchronized (connections)
		{
			for (Conn c : connections)
			{
				if (c.from == comp) return true;
			}
		}
		return false;
	}

	private void drawLoop()
	{
		while (running)
		{
			BufferedImage img = display.getCanvas();
			Graphics g = img.getGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, img.getWidth(), img.getHeight());

			//draw grid
			g.setColor(Color.black);

			if (gridMode == 0)
			{
				double d = 0;
				while (d < img.getWidth())
				{
					g.drawLine((int)d, 0, (int)d, img.getHeight());
					d += pixelRatio;
				}
				d = 0;
				while (d < img.getHeight())
				{
					g.drawLine(0, (int)d, img.getWidth(), (int)d);
					d += pixelRatio;
				}
			}
			else if (gridMode == 1)
			{
				for (double x = 0; x < img.getWidth(); x += pixelRatio)
				{
					for (double y = 0; y < img.getHeight(); y += pixelRatio)
					{
						img.setRGB((int)x, (int)y, Color.black.getRGB());
					}
				}
			}
			g.setFont(new Font("Arial", Font.BOLD, 22));

			//draw components
			synchronized (components)
			{
				LocComponent hover = null;
				int[] hoverNode = null;
				List<LocComponent> todraw = new ArrayList<>(components);
				if (tempComponent != null)
				{
					tempComponent.getLocation().x = (int) (mse.x * 1.0 / pixelRatio);
					tempComponent.getLocation().y = (int) (mse.y * 1.0 / pixelRatio);
					todraw.add(tempComponent);
				}
				for (int i = 0; i < todraw.size(); i++)
				{
					LocComponent component = todraw.get(i);
					Point p = component.getLocation();
					Dimension s = component.getSize();
					BufferedImage rot = Resources.rotateImage(component.getComponent().getImage(), component.getRotation());
					int xx = (int)(p.x * pixelRatio);
					int yy = (int)(p.y * pixelRatio);
					int wid = (int)(s.width * pixelRatio);
					int hei = (int)(s.height * pixelRatio);
					g.drawImage(rot, xx, yy, wid, hei, null);
					g.setColor(Color.yellow);
					if (component != tempComponent)
						g.drawString(i + "", xx + wid - 15, yy + 10);

					if (wiringMode)
					{
						int osize = 10;

						List<Double[]> dots = component.getDots();
						for (int j = 0; j < dots.size(); j++)
						{
							Double[] dot = dots.get(j);
							int xxx = xx + (int) (dot[0] * wid) - osize / 2;
							int yyy = yy + (int) (dot[1] * hei) - osize / 2;
							g.setColor(Color.green);
							if (mse.x >= xxx && mse.y >= yyy && mse.x < xxx + osize && mse.y < yyy + osize)
							{
								hoverNode = new int[] {i, j};
								g.setColor(Color.blue);
							}
							if (tempNode != null && tempNode[0] == i && tempNode[1] == j)
							{
								g.setColor(Color.pink);
							}
							g.fillOval(xxx, yyy, osize, osize);
						}
					}

					if (!wiringMode && tempComponent != component && mse.x >= xx && mse.y >= yy && mse.x < xx + wid && mse.y < yy + hei)
					{
						g.setColor(new Color(0, 0, 255, 128));
						g.fillRect(xx, yy, wid, hei);
						hover = component;
					}
				}
				hoverComponent = hover;
				this.hoverNode = hoverNode;
			}

			//draw connections
			synchronized (connections)
			{
				for (Conn conn : connections)
				{
					LocComponent from = conn.from;
					Point pf = from.getLocation();
					Dimension sf = from.getSize();
					int xxf = (int)(pf.x * pixelRatio);
					int yyf = (int)(pf.y * pixelRatio);
					int widf = (int)(sf.width * pixelRatio);
					int heif = (int)(sf.height * pixelRatio);
					List<Double[]> dots = from.getDots();
					Double[] dot = dots.get(dots.size() - 1);
					int xxxf = xxf + (int) (dot[0] * widf);
					int yyyf = yyf + (int) (dot[1] * heif);
					LocComponent to = conn.to;
					Point pt = to.getLocation();
					Dimension st = to.getSize();
					int xxt = (int)(pt.x * pixelRatio);
					int yyt = (int)(pt.y * pixelRatio);
					int widt = (int)(st.width * pixelRatio);
					int heit = (int)(st.height * pixelRatio);
					dot = to.getDots().get(conn.toNode);
					int xxxt = xxt + (int) (dot[0] * widt);
					int yyyt = yyt + (int) (dot[1] * heit);
					g.setColor(Color.black);
					g.drawLine(xxxf, yyyf, xxxt, yyyt);
				}
			}

			display.update(img);
		}
	}

	private void updateTempComponent(Class<? extends Component> clazz)
	{
		try
		{
			if (tempComponent == null)
			{
				tempComponent = new LocComponent(new Point(0, 0), new Dimension(4, 4), 0, clazz.newInstance());
			}
			else
			{
				tempComponent.setComponent(clazz.newInstance());
			}
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	private List<Conn> getConnectionsTo(LocComponent to, int toNode)
	{
		List<Conn> conns = new ArrayList<>();
		synchronized (connections)
		{
			for (Conn c : connections)
			{
				if (c.to == to && c.toNode == toNode)
				{
					conns.add(c);
				}
			}
		}
		return conns;
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == 1)
		{
			if (!wiringMode)
			{
				if (tempComponent != null)
				{
					components.add(tempComponent);
					tempComponent = null;
				}
				else if (hoverComponent != null)
				{
					components.remove(hoverComponent);
					tempComponent = hoverComponent;
					hoverComponent = null;
				}
			}
			else
			{
				if (hoverNode != null)
				{
					if (tempNode == null)
					{
						tempNode = hoverNode;
					}
					else
					{
						LocComponent a = components.get(tempNode[0]);
						LocComponent b = components.get(hoverNode[0]);
						if (b.isInput(hoverNode[1]) && !a.isInput(tempNode[1]))
						{
							if (getConnectionsTo(b, hoverNode[1]).size() == 0)
							{
								connections.add(new Conn(a, b, hoverNode[1]));
								tempNode = null;
							}
						}
						if (tempNode != null)
						{
							if (a.isInput(tempNode[1]) && !b.isInput(hoverNode[1]))
							{
								if (getConnectionsTo(a, tempNode[1]).size() == 0)
								{
									connections.add(new Conn(b, a, tempNode[1]));
									tempNode = null;
								}
							}
						}
					}
				}
			}
		}
		else if (e.getButton() == 3)
		{
			if (tempComponent != null)
			{
				tempComponent.setRotation((tempComponent.getRotation() + 1) & 3);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		Point off = display.getOffset();
		mse.x = e.getX() - off.x;
		mse.y = e.getY() - off.y;
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		Point off = display.getOffset();
		mse.x = e.getX() - off.x;
		mse.y = e.getY() - off.y;
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e)
	{
		int c = e.getKeyCode();

		System.out.println(c);
		if (c == 87)
		{
			if (tempComponent == null)
			{
				wiringMode = !wiringMode;
			}
		}

		if (!wiringMode)
		{
			if (c == '1')
			{
				updateTempComponent(AndGate.class);
			}
			else if (c == '2')
			{
				updateTempComponent(OrGate.class);
			}
			else if (c == '3')
			{
				updateTempComponent(NotGate.class);
			}
			else if (c == '4')
			{
				updateTempComponent(RelayGate.class);
			}
			else if (c == 27)
			{
				for (int i = connections.size() - 1; i >= 0; i--)
				{
					Conn cc = connections.get(i);
					if (cc.from == tempComponent || cc.to == tempComponent) connections.remove(i);
				}
				tempComponent = null;
			}
			else if (c == 84)
			{
				if (tempComponent == null)
				{
					Circuit cc = toCircuit();
					System.out.println(cc.toJson());
					System.out.println(Parser.calcTruthTable(toCircuit()));
				}
			}
		}
		else
		{
			if (c == 27)
			{
				tempNode = null;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
}
