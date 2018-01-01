package chess.model;

import java.util.ArrayList;
import java.util.List;

import chess.engine.BaseMove;
import chess.engine.Move;

public class Rook extends Piece {

	public Rook(Colour colour) {
		super(colour);
	}

	@Override
	public String getShortNotation() {
		return getColour() == Colour.white ? "R" : "r";
	}

	@Override
	public String getSymbol() {
		return getColour() == Colour.white ? "♖" : "♜";
	}

	@Override
	public List<Move> getPossibleMoves() {
		List<Move> moves = new ArrayList<>();
		Position[] positions = new Position[] { position, position, position, position};
		for (int i = 0; i < 4; i++) {
			positions[0] = positions[0].north();
			positions[1] = positions[1].east();
			positions[2] = positions[2].south();
			positions[3] = positions[3].west();
			for (Position p : positions)
				if (p != Position.illegalPosition)
					moves.add(new BaseMove(colour, getPosition(), p));
		}
		return moves;
	}

}
