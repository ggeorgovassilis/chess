package chess.model;

import java.util.ArrayList;
import java.util.List;

import chess.engine.BaseMove;
import chess.engine.Move;

public class Bishop extends Piece{

	public Bishop(Colour colour) {
		super(colour);
	}

	@Override
	public String getShortNotation() {
		return getColour()==Colour.white?"B":"b";
	}

	@Override
	public String getSymbol() {
		return getColour()==Colour.white?"♗":"♝";
	}

	@Override
	public List<Move> getPossibleMoves() {
		List<Move> moves = new ArrayList<>();
		Position[] positions = new Position[] {position,position,position,position};
		for (int i=0;i<8;i++) {
			positions[0] = positions[0].northEast();
			positions[1] = positions[1].southEast();
			positions[2] = positions[2].southWest();
			positions[3] = positions[3].northWest();
			for (Position p:positions)
				if (p!=Position.illegalPosition)
					moves.add(new BaseMove(colour, getPosition(), p));
		}
		return moves;
	}

}
