package chess.engine;

import chess.model.Piece.Colour;
import chess.model.Position;

public class BaseMove implements Move{

	protected final Position from;
	protected final Position to;
	protected final Colour player;
	
	public BaseMove(Colour player, Position from, Position to) {
		this.from = from;
		this.to = to;
		this.player = player;
	}
	
	@Override
	public Colour getPlayer() {
		return player;
	}
	
	@Override
	public Position getFrom() {
		return from;
	}
	
	@Override
	public Position getTo() {
		return to;
	}
	
	@Override
	public String toString() {
		return from+"-"+to;
	}

}
