package chess;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JComponent;


public class Board extends JComponent implements MouseListener, MouseMotionListener{

	int a; //this is the square that the mouse clicked
	int b; //this is the square that the mouse is released

	int recent1 = -1;
	int recent2 = -1;
	
	// this is used to unmake moves
	int pieceCaptured;
	
	boolean[] hasMoved = new boolean[64];

	boolean whiteTurn = true;

	ArrayList<Move> legalMoves = new ArrayList<Move>();

	static int[][] NumSquaresToEdge = new int[64][8];
	public static int[] DirectionOffSets = new int[]{-8, 8, -1, 1, -9, 9, -7, 7};

	//for the sound
	AudioInputStream audioInputStream;
	Clip moveClip;

	int pressedPiece; // the piece the user clicks on

	boolean holding = false;
	int dragX;
	int dragY;

	Square[] squares = new Square[64];
	int width = 100;
	int height = 100;
	
	Image whitePawn = getToolkit().getImage("whitePawn.png");
	Image blackPawn = getToolkit().getImage("blackPawn.png");
	Image whiteKnight = getToolkit().getImage("whiteKnight.png");
	Image blackKnight = getToolkit().getImage("blackKnight.png");
	Image whiteBishop = getToolkit().getImage("whiteBishop.png");
	Image blackBishop = getToolkit().getImage("blackBishop.png");
	Image whiteRook = getToolkit().getImage("whiteRook.png");
	Image blackRook = getToolkit().getImage("blackRook.png");
	Image whiteQueen = getToolkit().getImage("whiteQueen.png");
	Image blackQueen = getToolkit().getImage("blackQueen.png");
	Image whiteKing = getToolkit().getImage("whiteKing.png");
	Image blackKing = getToolkit().getImage("blackKing.png");


	public static int[] Tile;

	public Board() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		for (int i = 0; i < 64; i++) {
			squares[i] = new Square((i%8)*width, ((int)i/8)*height, width, height);
			squares[i].tileNumber = i;
		}
		
		//set all of the hasmoved to false
		for (int i = 0; i < 64; i++) {
			hasMoved[i] = false;
		}


		//set the starting position for white pawns
		for (int q = 48; q < 56; q++) {
			squares[q].piece = Piece.White | Piece.Pawn;
		}

		//set the starting position for black pawns
		for (int q = 8; q < 16; q++) {
			squares[q].piece = Piece.Black | Piece.Pawn;
		}

		//set all of the other starting positions
		squares[0].piece = Piece.Black | Piece.Rook;
		squares[7].piece = Piece.Black | Piece.Rook;
		squares[1].piece = Piece.Black | Piece.Knight;
		squares[6].piece = Piece.Black | Piece.Knight;
		squares[2].piece = Piece.Black | Piece.Bishop;
		squares[5].piece = Piece.Black | Piece.Bishop;
		squares[3].piece = Piece.Black | Piece.Queen;
		squares[4].piece = Piece.Black | Piece.King;

		squares[56].piece = Piece.White | Piece.Rook;
		squares[63].piece = Piece.White | Piece.Rook;
		squares[57].piece = Piece.White | Piece.Knight;
		squares[62].piece = Piece.White | Piece.Knight;
		squares[58].piece = Piece.White | Piece.Bishop;
		squares[61].piece = Piece.White | Piece.Bishop;
		squares[59].piece = Piece.White | Piece.Queen;
		squares[60].piece = Piece.White | Piece.King;

		//this is just to help me know which number goes with which piece
		System.out.println("White Pawn: " + (Piece.White | Piece.Pawn));
		System.out.println("Black Pawn: " + (Piece.Black | Piece.Pawn));
		System.out.println("White Knight: " + (Piece.White | Piece.Knight));
		System.out.println("Black Knight: " + (Piece.Black | Piece.Knight));
		System.out.println("White Bishop: " + (Piece.White | Piece.Bishop));
		System.out.println("Black Bishop: " + (Piece.Black | Piece.Bishop));
		System.out.println("White Rook: " + (Piece.White | Piece.Rook));
		System.out.println("Black Rook: " + (Piece.Black | Piece.Rook));
		System.out.println("White Queen: " + (Piece.White | Piece.Queen));
		System.out.println("Black Queen: " + (Piece.Black | Piece.Queen));
		System.out.println("White King: " + (Piece.White | Piece.King));
		System.out.println("Black King: " + (Piece.Black | Piece.King));

		//for the sound clip
		//		audioInputStream = AudioSystem.getAudioInputStream(new File("move.wav").getAbsoluteFile());
		//	
		//		moveClip = AudioSystem.getClip();
		//		
		//		moveClip.open(audioInputStream);

		for (int file = 0; file < 8; file++) {
			for (int rank = 0; rank < 8; rank++) {
				int squareIndex = rank * 8 + file;

				int numNorth = rank;
				int numSouth = 7 - rank;
				int numWest = file;
				int numEast = 7 - file;

				NumSquaresToEdge[squareIndex][0] = numNorth;
				NumSquaresToEdge[squareIndex][1] = numSouth;
				NumSquaresToEdge[squareIndex][2] = numWest;
				NumSquaresToEdge[squareIndex][3] = numEast;
				NumSquaresToEdge[squareIndex][4] = Math.min(numNorth, numWest);
				NumSquaresToEdge[squareIndex][5] = Math.min(numSouth, numEast);
				NumSquaresToEdge[squareIndex][6] = Math.min(numNorth, numEast);
				NumSquaresToEdge[squareIndex][7] = Math.min(numSouth, numWest);

			}
		}

		//		for (int j = 0; j < 64; j++) {
		//		System.out.println("The number of squares north of " + j + "is " + NumSquaresToEdge[j][0]);
		//		}
		legalMoves = GenerateLegalMoves();


	}

	public ArrayList<Move> GenerateMoves() {
		ArrayList<Move> moves = new ArrayList<Move>();

		for (int start = 0; start < 64; start ++) {
			int piece = squares[start].piece;
			if (piece > 0 && isTurn(piece)) {
				if (isSlidingPiece(piece)) {
					moves.addAll(generateSlidingMove(start, piece));
				}
				if (isPawn(piece)) {
					moves.addAll(generatePawnMove(start, piece));
				}
				if (isKnight(piece)) {
					moves.addAll(generateKnightMove(start, piece));
				}
				if (isKing(piece)) {
					moves.addAll(generateKingMove(start, piece));
				}
			}
		}

		//		for (int i= 0; i < 64; i++) {
		//			System.out.println(squares[i].piece);
		//		}
		return moves;
	}

	private ArrayList<Move> generateKingMove(int start, int piece) {
		
		ArrayList<Move> kingMoves = new ArrayList<Move>();
		
		for (int direction = 0; direction < 8; direction++) {
			if (NumSquaresToEdge[start][direction] > 0) {
				int targetSquare = start + DirectionOffSets[direction];
				int pieceOnTargetSquare = squares[targetSquare].piece;
				if (getColor(piece) != getColor(pieceOnTargetSquare)) {
					kingMoves.add(new Move(start, targetSquare));
				}
			}
		}
		if (hasMoved[start] == false) {
			if (hasMoved[start - 4] == false && squares[start - 3].piece == 0 && squares[start - 2].piece == 0 && squares[start - 1].piece == 0 ) {
				Move castle = new Move(start, start - 2);
				castle.castleLeft = true;
				kingMoves.add(castle);
			}
			if (hasMoved[start + 3] == false && squares[start + 2].piece == 0 && squares[start + 1].piece == 0 ) {
				Move castle = new Move(start, start + 2);
				castle.castleRight = true;
				kingMoves.add(castle);
			}
		}
		
		return kingMoves;
		
	}

	private boolean isKing(int piece) {
		if (piece == 14 || piece == 22) {
			return true;
		}
		return false;
	}

	private ArrayList<Move> generateKnightMove(int start, int piece) {
		
		ArrayList<Move> knightMoves = new ArrayList<Move>();

		if (piece == 10) {
			if (start>14 && NumSquaresToEdge[start][0] > 1 &&  NumSquaresToEdge[start][3] > 0) {
				if (getColor(squares[start - 15].piece) != 1) {
					knightMoves.add(new Move(start, start - 15));
				}
			}
			if (start>5 && NumSquaresToEdge[start][0] > 0 &&  NumSquaresToEdge[start][3] > 1) {
				if (getColor(squares[start - 6].piece) != 1) {
					knightMoves.add(new Move(start, start - 6));
				}
			}
			if (start<54 && NumSquaresToEdge[start][1] > 0 &&  NumSquaresToEdge[start][3] > 1) {
				if (getColor(squares[start + 10].piece) != 1) {
					knightMoves.add(new Move(start, start + 10));
				}
			}
			if (start < 47 && NumSquaresToEdge[start][1] > 1 &&  NumSquaresToEdge[start][3] > 0) {
				if (getColor(squares[start + 17].piece) != 1) {
					knightMoves.add(new Move(start, start + 17));
				}
			}
			if (start<49 && NumSquaresToEdge[start][1] > 1 &&  NumSquaresToEdge[start][2] > 0) {
				if (getColor(squares[start + 15].piece) != 1) {
					knightMoves.add(new Move(start, start + 15));
				}
			}
			if (start< 58 && NumSquaresToEdge[start][1] > 0 &&  NumSquaresToEdge[start][2] > 1) {
				if (getColor(squares[start + 6].piece) != 1) {
					knightMoves.add(new Move(start, start + 6));
				}
			}
			if (start>9 && NumSquaresToEdge[start][0] > 0 &&  NumSquaresToEdge[start][2] > 1) {
				if (getColor(squares[start - 10].piece) != 1) {
					knightMoves.add(new Move(start, start - 10));
				}
			}
			if (start>16 && NumSquaresToEdge[start][0] > 1 &&  NumSquaresToEdge[start][2] > 0) {
				if (getColor(squares[start - 17].piece) != 1) {
					knightMoves.add(new Move(start, start - 17));
				}
			}
		}
		if (piece == 18) {
			if (start>14 && NumSquaresToEdge[start][0] > 1 &&  NumSquaresToEdge[start][3] > 0) {
				if (getColor(squares[start - 15].piece) != 2) {
					knightMoves.add(new Move(start, start - 15));
				}
			}
			if (start>5 && NumSquaresToEdge[start][0] > 0 &&  NumSquaresToEdge[start][3] > 1) {
				if (getColor(squares[start - 6].piece) != 2) {
					knightMoves.add(new Move(start, start - 6));
				}
			}
			if (start<54 && NumSquaresToEdge[start][1] > 0 &&  NumSquaresToEdge[start][3] > 1) {
				if (getColor(squares[start + 10].piece) != 2) {
					knightMoves.add(new Move(start, start + 10));
				}
			}
			if (start < 47 && NumSquaresToEdge[start][1] > 1 &&  NumSquaresToEdge[start][3] > 0) {
				if (getColor(squares[start + 17].piece) != 2) {
					knightMoves.add(new Move(start, start + 17));
				}
			}
			if (start<49 && NumSquaresToEdge[start][1] > 1 &&  NumSquaresToEdge[start][2] > 0) {
				if (getColor(squares[start + 15].piece) != 2) {
					knightMoves.add(new Move(start, start + 15));
				}
			}
			if (start< 58 && NumSquaresToEdge[start][1] > 0 &&  NumSquaresToEdge[start][2] > 1) {
				if (getColor(squares[start + 6].piece) != 2) {
					knightMoves.add(new Move(start, start + 6));
				}
			}
			if (start>9 && NumSquaresToEdge[start][0] > 0 &&  NumSquaresToEdge[start][2] > 1) {
				if (getColor(squares[start - 10].piece) != 2) {
					knightMoves.add(new Move(start, start - 10));
				}
			}
			if (start>16 && NumSquaresToEdge[start][0] > 1 &&  NumSquaresToEdge[start][2] > 0) {
				if (getColor(squares[start - 17].piece) != 2) {
					knightMoves.add(new Move(start, start - 17));
				}
			}
		}	
		return knightMoves;

	}

	private boolean isKnight(int piece) {
		if (piece == 10 || piece == 18) {
			return true;
		}
		return false;
	}

	private ArrayList<Move> generatePawnMove(int start, int piece) {
		
		ArrayList<Move> pawnMoves = new ArrayList<Move>();
		// TODO Auto-generated method stub

		if (piece == 9 && start > 7) {
			if (squares[start-8].piece == 0) {
				Move newmove = new Move(start, start - 8);
				if (newmove.targetSquare < 8) {
					newmove.promotion = true;
				}
				pawnMoves.add(newmove);
			} if (getColor(squares[start-7].piece) == 2) {
				Move newmove = new Move(start, start - 7);
				if (newmove.targetSquare < 8) {
					newmove.promotion = true;
				}
				pawnMoves.add(newmove);
			}
		}
		if (piece == 9 && start > 8) {
			if (getColor(squares[start-9].piece) == 2) {
				Move newmove = new Move(start, start - 9);
				if (newmove.targetSquare < 8) {
					newmove.promotion = true;
				}
				pawnMoves.add(newmove);
			}
		}
		if (piece == 17 && start < 56) {
			if (squares[start+8].piece == 0) {
				Move newmove = new Move(start, start + 8);
				if (newmove.targetSquare > 55) {
					newmove.promotion = true;
				}
				pawnMoves.add(newmove);
			}
			if (getColor(squares[start+7].piece) == 1) {
				Move newmove = new Move(start, start + 7);
				if (newmove.targetSquare > 55) {
					newmove.promotion = true;
				}
				pawnMoves.add(newmove);
			}
		}
		if (piece == 17 && start < 55) {
			if (getColor(squares[start+9].piece) == 1) {
				Move newmove = new Move(start, start + 9);
				if (newmove.targetSquare > 55) {
					newmove.promotion = true;
				}
				pawnMoves.add(newmove);
			}
		}
		
		if (piece == 9 && hasMoved[start] == false && start > 47 && start < 56) {
			if( squares[start-8].piece == 0 && squares[start-16].piece == 0) {
				Move newmove = new Move(start, start - 16);
				if (newmove.targetSquare < 8) {
					newmove.promotion = true;
				}
				pawnMoves.add(newmove);
			}
		}
		
		if (piece == 17 && hasMoved[start] == false && start > 7 && start < 16) {
			if( squares[start+8].piece == 0 && squares[start+16].piece == 0) {
				Move newmove = new Move(start, start + 16);
				if (newmove.targetSquare > 55) {
					newmove.promotion = true;
				}
				pawnMoves.add(newmove);
			}
		}
		
		return pawnMoves;

	}

	private boolean isPawn(int piece) {
		// TODO Auto-generated method stub
		if (piece == 9 || piece == 17) {
			return true;
		}
		return false;
	}

	private ArrayList<Move> generateSlidingMove(int start, int piece) {

		ArrayList<Move> slidingMoves = new ArrayList<Move>();
		// moves for the queen
		if (piece == 13 || piece == 21) {

			for (int direction = 0; direction < 8; direction ++) {
				for (int n = 0; n < NumSquaresToEdge[start][direction]; n++) {
					int targetSquare = start + DirectionOffSets[direction] * (n+1);
					int pieceOnTargetSquare = squares[targetSquare].piece;

					//might be blocked by a friendly piece
					if (getColor(piece) == getColor(pieceOnTargetSquare)) {
						//						System.out.println(getColor(pieceOnTargetSquare));
						break;
					}

					slidingMoves.add(new Move(start, targetSquare));

					// this would be a capture
					if (getColor(piece) != getColor(pieceOnTargetSquare) && getColor(pieceOnTargetSquare) != 0) {
						break;
					}
				}
			}
		}

		// moves for the bishop
		if (piece == 11 || piece == 19) {

			for (int direction = 4; direction < 8; direction ++) {
				for (int n = 0; n < NumSquaresToEdge[start][direction]; n++) {
					int targetSquare = start + DirectionOffSets[direction] * (n+1);
					int pieceOnTargetSquare = squares[targetSquare].piece;

					//might be blocked by a friendly piece
					if (getColor(piece) == getColor(pieceOnTargetSquare)) {
						break;
					}

					slidingMoves.add(new Move(start, targetSquare));

					if (getColor(piece) != getColor(pieceOnTargetSquare) && getColor(pieceOnTargetSquare) != 0) {
						break;
					}
				}
			}
		}

		// moves for the rook
		if (piece == 12 || piece == 20) {

			for (int direction = 0; direction < 4; direction ++) {
				for (int n = 0; n < NumSquaresToEdge[start][direction]; n++) {
					int targetSquare = start + DirectionOffSets[direction] * (n+1);
					int pieceOnTargetSquare = squares[targetSquare].piece;

					//might be blocked by a friendly piece
					if (getColor(piece) == getColor(pieceOnTargetSquare)) {
						break;
					}

					slidingMoves.add(new Move(start, targetSquare));

					if (getColor(piece) != getColor(pieceOnTargetSquare) && getColor(pieceOnTargetSquare) != 0) {
						break;
					}
				}
			}
		}
		
		return slidingMoves;

	}

	private boolean isSlidingPiece(int piece) {
		if (piece == 11 || piece == 19 || piece == 12 || piece == 20 || piece == 13 || piece == 21) {
			return true;
		}
		return false;
	}

	public boolean isTurn(int piece) {
		if (whiteTurn) {
			if (piece < 17 ) {
				return true;
			} else {return false;
			}
		} else {
			if (piece > 16) {
				return true;
			} else {
				return false;
			}
		}
	}

	//white is 1 and black is 2 (empty is 0)
	public int getColor(int piece) {
		if (piece > 0 && piece < 17) {
			return 1;
		}
		if (piece > 16) {
			return 2;
		}
		return 0;
	}

	public void moveSound() throws LineUnavailableException, IOException, UnsupportedAudioFileException {

		audioInputStream = AudioSystem.getAudioInputStream(new File("move.wav").getAbsoluteFile());
		moveClip = AudioSystem.getClip();
		moveClip.open(audioInputStream);
		moveClip.start();

	}

	public void captureSound() throws LineUnavailableException, IOException, UnsupportedAudioFileException {

		audioInputStream = AudioSystem.getAudioInputStream(new File("capture.wav").getAbsoluteFile());
		moveClip = AudioSystem.getClip();
		moveClip.open(audioInputStream);
		moveClip.start();

	}

	@Override
	public void paintComponent( Graphics g ) {

		Graphics2D g2 = (Graphics2D)g;

		for (Square s : squares) {
			s.draw(g2);
		}

		for(int i = 0; i < 64 ; i++) {
			// add the rank and the file
			if ((i+((int)i/8))%2 == 0) {
				squares[i].fillWhite(g2);
			}
			else {
				squares[i].fillBlack(g2);
			}
		}
		
		for ( Square s : squares) {
			if (s.red == true) {
				if ((s.tileNumber + ((int) s.tileNumber/8)) % 2 == 0) {
					s.fillRed(g2);
				}
				else {
					s.fillDarkRed(g2);
				}
			}
		}

		if (recent1 >= 0 && recent2>=0) {
			if ((recent1 + ((int) recent1 / 8)) % 2 == 0) {
				squares[recent1].fillYellow(g2);
			} else {
				squares[recent1].fillYellowDark(g2);
			}
			if ((recent2 + ((int) recent2 / 8)) % 2 == 0) {
				squares[recent2].fillYellow(g2);
			} else {
				squares[recent2].fillYellowDark(g2);
			} 
		}
		for (int i= 0; i < 64; i++) {
			Square current = squares[i];
			if (current.piece == 9) {
				g.drawImage(whitePawn, current.x, current.y, current.width, current.height, getFocusCycleRootAncestor());
			}
			if (current.piece == 10) {
				g.drawImage(whiteKnight, current.x, current.y, current.width, current.height, getFocusCycleRootAncestor());
			}
			if (current.piece == 11) {
				g.drawImage(whiteBishop, current.x, current.y, current.width, current.height, getFocusCycleRootAncestor());
			}
			if (current.piece == 12) {
				g.drawImage(whiteRook, current.x, current.y, current.width, current.height, getFocusCycleRootAncestor());
			}
			if (current.piece == 13) {
				g.drawImage(whiteQueen, current.x, current.y, current.width, current.height, getFocusCycleRootAncestor());
			}
			if (current.piece == 14) {
				g.drawImage(whiteKing, current.x, current.y, current.width, current.height, getFocusCycleRootAncestor());
			}
			if (current.piece == 17) {
				g.drawImage(blackPawn, current.x, current.y, current.width, current.height, getFocusCycleRootAncestor());
			}
			if (current.piece == 18) {
				g.drawImage(blackKnight, current.x, current.y, current.width, current.height, getFocusCycleRootAncestor());
			}
			if (current.piece == 19) {
				g.drawImage(blackBishop, current.x, current.y, current.width, current.height, getFocusCycleRootAncestor());
			}
			if (current.piece == 20) {
				g.drawImage(blackRook, current.x, current.y, current.width, current.height - 10, getFocusCycleRootAncestor());
			}
			if (current.piece == 21) {
				g.drawImage(blackQueen, current.x, current.y, current.width, current.height, getFocusCycleRootAncestor());
			}
			if (current.piece == 22) {
				g.drawImage(blackKing, current.x, current.y, current.width, current.height, getFocusCycleRootAncestor());
			}
		}

		if (holding) {
			// this is for the piece that gets dragged
			if (pressedPiece == 9) {
				g.drawImage(whitePawn, dragX - 50, dragY - 50, width, height, getFocusCycleRootAncestor());
			}
			if (pressedPiece == 10) {
				g.drawImage(whiteKnight, dragX - 50, dragY - 50, width, height, getFocusCycleRootAncestor());
			}
			if (pressedPiece == 11) {
				g.drawImage(whiteBishop, dragX - 50, dragY - 50, width, height, getFocusCycleRootAncestor());
			}
			if (pressedPiece == 12) {
				g.drawImage(whiteRook, dragX - 50, dragY - 50, width, height, getFocusCycleRootAncestor());
			}
			if (pressedPiece == 13) {
				g.drawImage(whiteQueen, dragX - 50, dragY - 50, width, height, getFocusCycleRootAncestor());
			}
			if (pressedPiece == 14) {
				g.drawImage(whiteKing, dragX - 50, dragY - 50, width, height, getFocusCycleRootAncestor());
			}
			if (pressedPiece == 17) {
				g.drawImage(blackPawn, dragX - 50, dragY - 50, width, height, getFocusCycleRootAncestor());
			}
			if (pressedPiece == 18) {
				g.drawImage(blackKnight, dragX - 50, dragY - 50, width, height, getFocusCycleRootAncestor());
			}
			if (pressedPiece == 19) {
				g.drawImage(blackBishop, dragX - 50, dragY - 50, width, height, getFocusCycleRootAncestor());
			}
			if (pressedPiece == 20) {
				g.drawImage(blackRook, dragX - 50, dragY - 50, width, height - 10, getFocusCycleRootAncestor());
			}
			if (pressedPiece == 21) {
				g.drawImage(blackQueen, dragX - 50, dragY - 50, width, height, getFocusCycleRootAncestor());
			}
			if (pressedPiece == 22) {
				g.drawImage(blackKing, dragX - 50, dragY - 50, width, height, getFocusCycleRootAncestor());
			} 
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		//		System.out.println((int) e.getX()/100);
		//		System.out.println(8 * ((int) e.getY()/100));


	}

	@Override
	public void mousePressed(MouseEvent e) {

		a = (int) e.getX()/100 + 8 * ((int) e.getY()/100);
		pressedPiece = squares[a].piece;
		squares[a].piece = 0;
		holding = true;
		recent1 = a;
		recent2 = -1;
		for (Move m : legalMoves) {
			if (m.startSquare == a) {
				squares[m.targetSquare].red = true;
			}
		}

	}

	public void move(int start, int end) {
		if (squares[end].piece != 0) {
			try {
				this.captureSound();
			} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			try {
				this.moveSound();
			} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		squares[end].piece = pressedPiece;
		squares[start].piece = 0;
		holding = false;
		recent2 = b;
		this.repaint();
		whiteTurn = !whiteTurn;
		hasMoved[start] = true;
		hasMoved[end] = true;
		pressedPiece = 0;
	}
	
	public int getOpponentKingSquare() {
		for (Square s: squares) {
			if (whiteTurn) {
				if (s.piece == 22) {
					return s.tileNumber;
				}
			}
			if (whiteTurn == false) {
				if (s.piece == 14) {
					return s.tileNumber;
				}
			}
		}
		return 0;
	}

	private ArrayList<Move> GenerateLegalMoves() {
		
		//goes to false if the opponent responses found any king captures
		boolean islegal = true;
		
		boolean rememberTurn = whiteTurn;
		// first generate all moves not accounting for check
		ArrayList<Move> psuedoLegalMoves = GenerateMoves();
		ArrayList<Move> legal = new ArrayList<Move>();
		for (Move m : psuedoLegalMoves) {
			islegal = true; //first assume the move is legal
			testmove(m.startSquare, m.targetSquare);
			ArrayList<Move> opponentResponses = GenerateMoves();
			int kingSquare = getOpponentKingSquare();
//			System.out.println(kingSquare);
			for (Move n : opponentResponses) {
				if (n.targetSquare == kingSquare) {
					islegal = false; //the opponent captured my king! (last move was illegal)
				} 
			}
			if (islegal == true) {
				legal.add(m);
			}
			UnmakeTestMove(m.startSquare, m.targetSquare);
		}
		whiteTurn = rememberTurn;
		return legal;
	}

	private void testmove(int startSquare, int targetSquare) {
		// TODO Auto-generated method stub
		pieceCaptured = squares[targetSquare].piece;
		squares[targetSquare].piece = squares[startSquare].piece;
		squares[startSquare].piece = 0;
		whiteTurn = !whiteTurn;
	}
	
	private void UnmakeTestMove(int startSquare, int targetSquare) {
		squares[startSquare].piece = squares[targetSquare].piece;
		squares[targetSquare].piece = pieceCaptured;
		whiteTurn = !whiteTurn;
	}

	public boolean isLegalMove(int start, int end) {
		for (Move m: legalMoves) {
			if (m.startSquare == start && m.targetSquare == end) {
				return true;
			}
		} return false;
	}
	
	public boolean isCastleLeft(int start, int end) {
		for (Move m: legalMoves) {
			if (m.startSquare == start && m.targetSquare == end) {
				if (m.castleLeft) {
					return true;
				}
			}
		} return false;
	}
	
	public boolean isCastleRight(int start, int end) {
		for (Move m: legalMoves) {
			if (m.startSquare == start && m.targetSquare == end) {
				if (m.castleRight) {
					return true;
				}
			}
		} return false;
	}

	@Override
	public void mouseReleased(MouseEvent e) {

		b = (int) e.getX()/100 + 8 * ((int) e.getY()/100);
		if (isLegalMove(a,b)) {
			move(a, b);
			if (isCastleLeft(a, b)) {
				if (getColor(squares[b].piece) == 1) {
					squares[b +1].piece = 12;
					squares[b - 2].piece = 0;
				}
				if (getColor(squares[b].piece) == 2) {
					squares[b + 1].piece = 20;
					squares[b - 2].piece = 0;
				}
			}
			if (isCastleRight(a, b)) {
				if (getColor(squares[b].piece) == 1) {
					squares[b - 1].piece = 12;
					squares[b + 1].piece = 0;
				}
				if (getColor(squares[b].piece) == 2) {
					squares[b - 1].piece = 20;
					squares[b + 1].piece = 0;
				}
			}
			if (isPromotion(a,b)) {
				if( getColor(squares[b].piece) == 1) {
					squares[b].piece = 13;
				}
				if( getColor(squares[b].piece) == 2) {
					squares[b].piece = 21;
				}
			}
		} else { squares[a].piece = pressedPiece;
		}
		holding = false;
		pressedPiece = 0;
		for (Square s : squares) {
			s.red = false;
		}
		this.repaint();
		legalMoves = GenerateLegalMoves();
		if (legalMoves.isEmpty()) {
			if (whiteTurn) {
				System.out.println("Black wins!");
			} else {
				System.out.println("White wins!");
			}
		}
	}

	private boolean isPromotion(int a2, int b2) {
		for (Move m: legalMoves) {
			if (m.startSquare == a2 && m.targetSquare == b2) {
				if (m.promotion) {
					return true;
				}
			}
		} return false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {

		dragX = e.getX();
		dragY = e.getY();
		//			System.out.println(dragX);
		//			System.out.println(dragY);
		this.repaint();

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}

}
