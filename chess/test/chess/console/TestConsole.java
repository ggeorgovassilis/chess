package chess.console;

import org.junit.Before;
import org.junit.Test;

import chess.engine.BoardFactory;
import chess.engine.Engine;
import chess.model.Board;
import chess.ui.Console;

public class TestConsole {
	

	Console console;
	Engine engine;
	BoardFactory boardFactory;
	
	@Before
	public void setup() {
		this.boardFactory = new BoardFactory();
		Board board = boardFactory.createStandardSetup();
		this.engine = new Engine();
		engine.setBoard(board);
		this.console = new Console(engine);
	}

	@Test
	public void testConsole() {
		console.run();
	}
}
