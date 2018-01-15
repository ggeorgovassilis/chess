package chess;

import java.util.ArrayList;
import java.util.List;

import chess.engine.BoardFactory;
import chess.engine.Engine;
import chess.engine.SimpleEngineImpl;
import chess.model.Board;
import chess.model.Piece.Colour;
import chess.ui.Command;
import chess.ui.Console;

public class Chess {

	Console console;
	Engine engine;

	void warmUp() {
		List<Thread> heaters = new ArrayList<>();
		for (int i=0;i<4;i++) {
			final Colour c = i%2==0?Colour.white:Colour.black;
			Thread t = new Thread() {
				@Override
				public void run() {
					BoardFactory bf = new BoardFactory();
					Board board = bf.createStandardSetup();
					Engine engine = new SimpleEngineImpl(board);
					engine.getBestMoveFor(c);
				}
			};
			t.start();
			heaters.add(t);
		}
		for (Thread t:heaters)
			try {
				t.join();
			} catch (InterruptedException e) {
			}
	}

	public Chess() {
		//warming up
		//warmUp();
		BoardFactory bf = new BoardFactory();
		Board board = bf.createStandardSetup();
		engine = new SimpleEngineImpl(board);
		console = new Console(engine);
	}

	public void run() {
		console.drawBoard();
		console.drawPieces();
		console.flush();
		while (console.isOpen()) {
			Command command = console.getNextCommand();
			try {
				command.execute();
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
	}

	public static void main(String[] args) {
		Chess chess = new Chess();
		chess.run();
	}
}
