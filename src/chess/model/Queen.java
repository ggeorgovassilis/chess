package chess.model;

import java.util.ArrayList;
import java.util.List;

import chess.engine.BaseMove;
import chess.engine.Move;

public class Queen extends Piece {

	public Queen(Colour colour) {
		super(colour);
	}

	@Override
	public String getShortNotation() {
		return getColour() == Colour.white ? "Q" : "q";
	}

	@Override
	public String getSymbol() {
		return getColour() == Colour.white ? "♕" : "♛";
	}

	@Override
	public List<Move> getPossibleMoves() {
		List<Move> moves = new ArrayList<>();
		Position[] positions = new Position[] { position, position, position, position, position, position, position,
				position };
		for (int i = 0; i < 8; i++) {
			positions[0] = positions[0].north();
			positions[1] = positions[1].northEast();
			positions[2] = positions[2].east();
			positions[3] = positions[3].southEast();
			positions[4] = positions[4].south();
			positions[5] = positions[5].southWest();
			positions[6] = positions[6].west();
			positions[7] = positions[7].northWest();
			for (Position p : positions)
				if (p != Position.illegalPosition)
					moves.add(new BaseMove(colour, getPosition(), p));
		}
		return moves;
	}

}
