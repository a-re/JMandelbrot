package co.restifo.mandelbrot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;


public class MandelFrame extends JPanel
{
	Point.Double[][] pixels;
	int[] palette;
	int x;
	int y;
	int i = 1;
	int MAX_ITERS;
	double scale = 1.0;
	private static final long serialVersionUID = 1L;
	private final double LOG2 = log2(2.0);
	public MandelFrame(int x, int y, int MAX_ITERS)
	{
		setPreferredSize(new Dimension(x, y));
		this.MAX_ITERS = MAX_ITERS;
		this.x = x;
		this.y = y;
		pixels = new Point.Double[x][y];
		double sPixX = 3.5 / x; // scaled pixel size of X in mandelbrot X scale (-2.5,1)
		double sPixY = 2.0 / y; // scaled pixel size of Y in mandelbrot Y scale (-1,1)
		for (int xI = 0; xI < x; xI++)
		{
			for (int yI = 0; yI < y; yI++)
			{
				double pX = -2.5 + sPixX * xI;
				double pY = -1.0 + sPixY * yI;
				pixels[xI][yI] = new Point.Double(pX, pY);
			}
		}
		generatePalette();
	}
	
	@Override
	public void paint(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		BufferedImage image = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
		for (int xI = 0; xI < x; xI++)
		{
			for (int yI = 0; yI < y; yI++)
			{
				Point2D.Double px = pixels[xI][yI];
				int color = calculate(px.x / scale - 1.5, px.y / scale, i) - 1;
				image.setRGB(xI, yI, palette[color]);
			}
		}
		g.drawImage(image, 0, 0, null);
		
		scale += Math.pow(1.1, i);
		i++;
	}
	
	public int calculate(double sX, double sY, int iters)
	{
		double xC = 0.0;
		double yC = 0.0;
		int it = 0;
		final int BAILOUT = 4;
		while ((xC * xC + yC * yC < BAILOUT) && (it < iters))
		{
			double xT = xC * xC - yC * yC + sX;
			yC = 2 * xC * yC + sY;
			xC = xT;
			it++;
		}
		
		if (it < iters)
		{
			int log_i = (int) (log2(xC * xC + yC * yC) / 2.0);
			int subI = (int) (log2(log_i / LOG2) / LOG2);
			it = it + 1 - subI;
		}
		return it == iters ? 1 : it;
	}
	
	private double log2(double x)
	{
		return Math.log(x) / Math.log(2.0);
	}
	
	private void generatePalette()
	{
		palette = new int[MAX_ITERS + 2];
		palette[0] = Color.black.getRGB();
		for(int n = 1; n < palette.length; n++) 
		{
			// use sine to model a "rainbow wave" and use two different phases to offset the initial color (math.pi & math.pi/2)
	        palette[n] = (int)(Math.sin(n) * 127 + 128) << 16 | 
	        		     (int)(Math.sin(n + Math.PI / 2) * 127 + 128) << 8 | 
	        			 (int)(Math.sin(n + Math.PI) * 127 + 128) | 0xFF000000; //0xFF000000 is 100% alpha
	    }
	}
}
