package chess.model;

import java.util.ArrayList;
import java.util.List;

import chess.engine.BaseMove;
import chess.engine.Engine;
import chess.engine.Move;
import chess.engine.ValidatedMove;

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
	protected MoveProducer generateMoves() {
		return new MoveProducer() {

			Position[] positions;

			@Override
			int getMaxMoveCounter() {
				return 32;
			}

			@Override
			protected void initialise() {
				Position p = getPosition();
				positions = new Position[] { p, p, p, p };
			}

			@Override
			Position getDestinationPosition(int moveCounter) {
				int p = moveCounter % positions.length;
				if (p == 0) {
					positions[0] = positions[0].north();
					positions[1] = positions[1].east();
					positions[2] = positions[2].south();
					positions[3] = positions[3].west();
				}
				return positions[p];
			}
		};
	}

	@Override
	public void validateMove(ValidatedMove vm, Engine engine) throws IllegalMove {
		validateContinuousMove(vm, (dCol, dRow) -> true, (dCol, dRow) -> dRow * dCol == 0);
	}

	@Override
	public boolean canTake(Piece target, Board board) {
		int dc = target.getPosition().column - getPosition().column;
		int dr = target.getPosition().row - getPosition().row;
		if (dc * dr != 0)
			return false;
		dr = normaliseDirection(dr);
		dc = normaliseDirection(dc);
		return canTake(target, board, dc, dr);
	}

}
