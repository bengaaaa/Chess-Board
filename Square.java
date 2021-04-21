package chess;

import java.awt.Color;
import java.awt.Graphics2D;

public class Square {

	int x;
	int y;
	int width;
	int height;
	
	// an easy way to get the tile number
	int tileNumber;

	//to see if the square should be red to show legal moves
	boolean red;

	boolean recent;

	int piece;

	static Color outline = new Color(100, 22, 45);
	static Color green = new Color( 95, 158, 160);
	static Color pale = new Color( 224, 255, 255);
	static Color yellow = new Color(240, 215, 163);
	static Color yellowDark = new Color(195, 170, 120);
	static Color lightCoral = new Color(240, 128, 128);
	static Color indianRed = new Color(205, 92, 92);


	// in the constructor we have the x,y coordinate of the top left corner
	// the width and height of the square
	// and the 

	public Square(int x, int y, int width, int height) {

		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		recent = false;
		red = false;
	}

	public void draw(Graphics2D g2) {

		g2.setColor(outline);

		g2.drawRect(x, y, width, height);

	}

	public void fillBlack(Graphics2D g2) {

		g2.setColor(green);

		g2.fill3DRect(x, y, width, height, false);
	}

	public void fillWhite(Graphics2D g2) {

		g2.setColor(pale);

		g2.fill3DRect(x, y, width, height, false);
	}

	public void fillYellow(Graphics2D g2) {

		g2.setColor(yellow);

		g2.fill3DRect(x, y, width, height, false);

	}

	public void fillYellowDark(Graphics2D g2) {

		g2.setColor(yellowDark);

		g2.fill3DRect(x, y, width, height, false);

	}

	public void fillRed(Graphics2D g2) {
		// TODO Auto-generated method stub
		g2.setColor(lightCoral);

		g2.fill3DRect(x, y, width, height, false);
	}

	public void fillDarkRed(Graphics2D g2) {
		// TODO Auto-generated method stub
		g2.setColor(indianRed);

		g2.fill3DRect(x, y, width, height, false);
	}

}
