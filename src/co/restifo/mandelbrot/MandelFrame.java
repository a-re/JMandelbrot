package co.restifo.mandelbrot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JPanel;

import org.apfloat.Apfloat;

@SuppressWarnings("serial")
public class MandelFrame extends JPanel {
	private double[][][] pixels;
	private int[] palette;
	private int x;
	private int y;
	private int i = 1;
	private int MAX_ITERS;
	private volatile Apfloat scale = Apfloat.ONE;
	private final Apfloat BD_BAILOUT = new Apfloat(4.0);
	private final Apfloat BD_TWO = new Apfloat(2.0);
	private final Apfloat BD_SCALE_CONST = new Apfloat(1.5);
	private final ExecutorService threadPool;
	
	public MandelFrame(int x, int y, int MAX_ITERS) {
		setPreferredSize(new Dimension(x, y));
		this.MAX_ITERS = MAX_ITERS;
		this.x = x;
		this.y = y;
		this.pixels = new double[x][y][2];
		this.threadPool = Executors.newCachedThreadPool((Runnable r) -> {
			Thread thread = new Thread(r);
			thread.setPriority(Thread.MIN_PRIORITY);
			return thread;
		});
		double sPixX = 3.5 / x; // scaled pixel size of X in mandelbrot X scale (-2.5,1)
		double sPixY = 2.0 / y; // scaled pixel size of Y in mandelbrot Y scale (-1,1)
		for (int xI = 0; xI < x; xI++) {
			for (int yI = 0; yI < y; yI++) {
				pixels[xI][yI][0] = -2.5 + sPixX * xI;
				pixels[xI][yI][1] = -1.0 + sPixY * yI;
			}
		}
		generatePalette();
	}
	
	@Override
	public void paint(Graphics gr) {
		Graphics2D g = (Graphics2D) gr;
		BufferedImage image = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
		List<Future<Integer[]>> threads = new ArrayList<>();
		for (int xI = 0; xI < x; xI++) {
			for (int yI = 0; yI < y; yI++) {
				final double[] px = pixels[xI][yI];
				final int xIf = xI;
				final int yIf = yI;
				threads.add(threadPool.submit(() -> {
					int color = calculate(new Apfloat(px[0], Apfloat.INFINITE).divide(scale/*, RoundingMode.HALF_UP*/).subtract(BD_SCALE_CONST), 
							              new Apfloat(px[1], Apfloat.INFINITE).divide(scale/*, RoundingMode.HALF_UP*/), i);
					return new Integer[] {xIf, yIf, palette[color]};
				}));
			}
		}
		
		for (Future<Integer[]> t : threads) {
			try { 
				Integer[] imgVals = t.get();
				image.setRGB(imgVals[0], imgVals[1], imgVals[2]);
			} catch (Exception e) {}
		}
		g.drawImage(image, 0, 0, null);
		
		scale = scale.multiply(new Apfloat(1.1));
		i++;
	}
	
	public int calculate(Apfloat sX, Apfloat sY, int iters) {
		Apfloat xC = Apfloat.ZERO;
		Apfloat yC = Apfloat.ZERO;
		int it = 0;
		while ((BD_BAILOUT.compareTo(xC.multiply(xC).add(yC.multiply(yC))) > 0) && (it < iters)) {
			Apfloat xT = xC.multiply(xC).subtract(yC.multiply(yC)).add(sX);
			yC = BD_TWO.multiply(xC).multiply(yC).add(sY);
			xC = xT;
			it++;
		}
		return it == iters ? 0 : it;
	}
	
	private void generatePalette() {
		palette = new int[MAX_ITERS + 2];
		palette[0] = Color.black.getRGB();
		for(int n = 1; n < palette.length; n++) {
			// use sine to model a "rainbow wave" and use two different phases to offset the initial color (math.pi & math.pi/2)
	        palette[n] = (int)(Math.sin(n) * 127 + 128) << 16 | 
	        		     (int)(Math.sin(n + Math.PI / 2) * 127 + 128) << 8 | 
	        			 (int)(Math.sin(n + Math.PI) * 127 + 128); //| 0xFF000000; //0xFF000000 is 100% alpha
	    }
	}
}
