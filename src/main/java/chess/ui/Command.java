package chess.ui;

import chess.engine.Engine;
import chess.model.Piece.Colour;

public abstract class Command {

	protected Engine engine;
	protected Console console;
	
	protected Command(Engine engine, Console console) {
		this.engine = engine;
		this.console = console;
	}
	
	protected Colour getOpponent(Colour player) {
		return player==Colour.white?Colour.black:Colour.white;
	}
	
	public abstract void execute();
}
