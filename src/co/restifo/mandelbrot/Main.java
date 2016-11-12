package co.restifo.mandelbrot;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Main {
	static int x = 400; //600
	static int y = 400; //600
	static int MAX_ITERS = 100000;
	public static void main(String[] args) throws InterruptedException
	{
		JFrame frame = new JFrame("Mandelbrot");
		MandelFrame mandel = new MandelFrame(x, y, MAX_ITERS);
		frame.setSize(new Dimension(x, y));
		frame.add(mandel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		
		frame.setVisible(true);
		
		for (int i = 0; i < MAX_ITERS; i++)
		{
			frame.repaint();
			Thread.sleep(1000 / 30);
		}
	}
}
