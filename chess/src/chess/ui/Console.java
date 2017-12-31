package chess.ui;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chess.engine.Engine;
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

	public Console(Engine engine) {
		this.engine = engine;
		lnr = new LineNumberReader(new InputStreamReader(System.in));
	}

	void printRow(int row, String s[], Board board) {
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

	public void printBoard() {
		// @formatter:off
		String s[] = new String[] { 
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
		for (int row = 7; row >= 0; row--) {
			printRow(row, s, engine.getBoard());
		}
		for (String line : s)
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

	public void run() {
		try {
			while (isopen) {
				printBoard();
				System.out.print(">");
				String line = lnr.readLine();
				Command command = readCommand(line);
				execute(command);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
