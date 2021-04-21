package chess;

public class Move {

	public int startSquare;
	public int targetSquare;
	
	public boolean castleLeft;
	public boolean castleRight;
	
	public boolean promotion;
	
	public Move( int start, int target) {
		this.startSquare = start;
		this.targetSquare = target;
		this.castleLeft = false;
		this.castleRight = false;
		this.promotion = false;
	}
	
	public int getStartSquare() {
		return startSquare;
	}
	public int getTargetSquare() {
		return getTargetSquare();
	}
	
}
