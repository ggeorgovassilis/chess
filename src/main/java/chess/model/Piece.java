package chess.model;

import java.util.List;

import chess.engine.Move;

public abstract class Piece {

	public enum Colour {
		black, white
	};

	protected final Colour colour;
	protected Position position;

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public abstract String getShortNotation();
	public abstract String getSymbol();

	protected Piece(Colour colour) {
		this.colour = colour;
	}

	public Colour getColour() {
		return colour;
	}
	
	@Override
	public String toString() {
		return getShortNotation();
	}
	
	public abstract List<Move> getPossibleMoves();
}
