package chess;

import chess.engine.BoardFactory;
import chess.engine.Engine;
import chess.model.Board;
import chess.ui.Console;

public class Chess {

	Console console;
	Engine engine;
	
	public Chess() {
		BoardFactory bf = new BoardFactory();
		Board board = bf.createStandardSetup();
		engine = new Engine();
		engine.setBoard(board);
		console = new Console(engine);
	}
	
	public void run() {
		console.run();
	}
	
	public static void main(String[] args) {
		Chess chess = new Chess();
		chess.run();
	}
}
