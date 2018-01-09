package chess.ui;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chess.engine.Engine;
import chess.engine.ValidatedMove;
import chess.model.Board;
import chess.model.IllegalMove;
import chess.model.Piece;
import chess.model.Piece.Colour;
import chess.model.Position;

public class Console implements Closeable {

	Engine engine;
	boolean isopen = true;
	Colour player = Colour.white;
	LineNumberReader lnr;
	String[] screen = new String[0];
	ValidatedMove lastMove;

	public ValidatedMove getLastMove() {
		return lastMove;
	}

	public void setLastMove(ValidatedMove lastMove) {
		this.lastMove = lastMove;
	}

	public Console(Engine engine) {
		this.engine = engine;
		lnr = new LineNumberReader(new InputStreamReader(System.in));
	}

	void drawPiecesInRow(int row, String s[], Board board) {
		int sRow = 2 + (7 - row) * 2;
		for (int column = 0; column < 8; column++) {
			int sColumn = 3 + column * 3;
			String line = s[sRow];
			Piece p = board.getPieceAt(Position.position(column, row));
			if (p != null)
				line = line.substring(0, sColumn) + p.getShortNotation() + line.substring(sColumn + 1);
			s[sRow] = line;
		}
	}

	public void drawBoard() {
		// @formatter:off
		screen = new String[] { 
				"   a  b  c  d  e  f  g  h ", 
				"     ▄▄▄   ▄▄▄   ▄▄▄   ▄▄▄",
				"8    ███   ███   ███   ███", 
				"  ▄▄▄▀▀▀▄▄▄▀▀▀▄▄▄▀▀▀▄▄▄▀▀▀", 
				"7 ███   ███   ███   ███   ",
				"  ▀▀▀▄▄▄▀▀▀▄▄▄▀▀▀▄▄▄▀▀▀▄▄▄", 
				"6    ███   ███   ███   ███", 
				"  ▄▄▄▀▀▀▄▄▄▀▀▀▄▄▄▀▀▀▄▄▄▀▀▀",
				"5 ███   ███   ███   ███   ", 
				"  ▀▀▀▄▄▄▀▀▀▄▄▄▀▀▀▄▄▄▀▀▀▄▄▄", 
				"4    ███   ███   ███   ███",
				"  ▄▄▄▀▀▀▄▄▄▀▀▀▄▄▄▀▀▀▄▄▄▀▀▀", 
				"3 ███   ███   ███   ███   ", 
				"  ▀▀▀▄▄▄▀▀▀▄▄▄▀▀▀▄▄▄▀▀▀▄▄▄",
				"2    ███   ███   ███   ███", 
				"  ▄▄▄▀▀▀▄▄▄▀▀▀▄▄▄▀▀▀▄▄▄▀▀▀", 
				"1 ███   ███   ███   ███   ",
				"  ▀▀▀   ▀▀▀   ▀▀▀   ▀▀▀   ", 
				"   a  b  c  d  e  f  g  h " };
		// @formatter:on

	}

	public void drawPieces() {
		for (int row = 7; row >= 0; row--) {
			drawPiecesInRow(row, screen, engine.getBoard());
		}
	}

	public void markPositions(List<Position> positions) {
		for (Position p : positions) {
			int start = 2 + p.column * 3;
			int end = start + 3;

			String line = screen[(7 - p.row) * 2 + 2 - 1];
			screen[(7 - p.row) * 2 + 2 - 1] = line.substring(0, start) + "┌─┐" + line.substring(end);

			line = screen[(7 - p.row) * 2 + 2];
			screen[(7 - p.row) * 2 + 2] = line.substring(0, start) + "│ │" + line.substring(end);

			line = screen[(7 - p.row) * 2 + 2 + 1];
			screen[(7 - p.row) * 2 + 2 + 1] = line.substring(0, start) + "└─┘" + line.substring(end);

		}
	}

	public void flush() {
		for (String line : screen)
			System.out.println(line);
	}

	@Override
	public void close() {
		isopen = false;
		try {
			lnr.close();
		} catch (IOException e) {
		}
	}

	Command readCommand(String line) {
		// quit?
		if (line == null)
			return new QuitCommand(engine, this);
		Pattern quitPattern = Pattern.compile("q");
		Matcher m = quitPattern.matcher(line);
		if (m.matches())
			return new QuitCommand(engine, this);

		// move?
		Pattern movePattern = Pattern.compile("([abcdefgh][12345678])\\s?([abcdefgh][12345678])");
		m = movePattern.matcher(line);
		if (m.matches())
			return new MoveCommand(engine, player, this, m.group(1), m.group(2));
		// get valid moves?
		Pattern getValidMovesPattern = Pattern.compile("\\?([abcdefgh][12345678])");
		m = getValidMovesPattern.matcher(line);
		if (m.matches())
			return new FindValidMovesCommand(engine, this, m.group(1));

		// undo?
		Pattern getUndoPattern = Pattern.compile("undo");
		m = getUndoPattern.matcher(line);
		if (m.matches())
			return new UndoCommand(engine, this);
		return new UnknownCommand(engine, this);
	}

	public void println(String msg) {
		System.out.println(msg);
	}

	public void error(String msg) {
		System.err.println(msg);
		System.err.flush();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}
	}

	void execute(Command command) {
		try {
			command.execute();
		} catch (IllegalMove e) {
			error(e.getMessage());
		}
	}
	
	public boolean isOpen() {
		return isopen;
	}

	public Command getNextCommand() {
		try {
			if (!isopen)
				return null;
			System.out.print(">");
			String line = lnr.readLine();
			Command command = readCommand(line);
			return command;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
