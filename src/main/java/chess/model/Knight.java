package chess.model;

import java.util.ArrayList;
import java.util.List;

import chess.engine.BaseMove;
import chess.engine.Move;

public class Knight extends Piece {

	public Knight(Colour colour) {
		super(colour);
	}

	@Override
	public String getShortNotation() {
		return getColour() == Colour.white ? "N" : "n";
	}

	@Override
	public String getSymbol() {
		return getColour() == Colour.white ? "♘" : "♞";
	}

	@Override
	public List<Move> getPossibleMoves() {
		List<Move> moves = new ArrayList<>();
		Position[] positions = new Position[] { position, position, position, position, position, position, position,
				position };
		for (int i = 0; i < 1; i++) {
			positions[0] = positions[0].north().northWest();
			positions[1] = positions[1].north().northEast();
			positions[2] = positions[2].east().northEast();
			positions[3] = positions[3].east().southEast();
			positions[4] = positions[4].south().southEast();
			positions[5] = positions[5].south().southWest();
			positions[6] = positions[6].west().southWest();
			positions[7] = positions[7].west().northWest();
			for (Position p : positions)
				if (p != Position.illegalPosition)
					moves.add(new BaseMove(colour, getPosition(), p));
		}
		return moves;
	}

}
