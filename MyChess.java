package chess;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

public class MyChess {

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		// TODO Auto-generated method stub
		
		new MyChess().run();

	}

	private void run() throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		
		JFrame frame = new JFrame("Chess");
		
		frame.setSize(815,835);
		frame.setLocation(600, 100);
		
		Board board = new Board();
		
		frame.add(board);
		
		board.addMouseListener(board);
		
		board.addMouseMotionListener(board);
		
		frame.setVisible(true);
		
	}

}
