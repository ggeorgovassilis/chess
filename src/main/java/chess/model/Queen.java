package chess.model;

import chess.engine.Engine;
import chess.engine.ValidatedMove;

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
	protected MoveProducer generateMoves() {
		return new MoveProducer(getPosition()) {

			Position[] positions;

			@Override
			protected void initialise() {
				Position p = startingPosition;
				positions = new Position[] { p, p, p, p, p, p, p, p };
			}

			@Override
			int getMaxMoveCounter() {
				return 48;
			}

			@Override
			Position getDestinationPosition(int moveCounter) {
				int p = moveCounter % positions.length;
				if (p == 0) {
					positions[0] = positions[0].north();
					positions[1] = positions[1].northEast();
					positions[2] = positions[2].east();
					positions[3] = positions[3].southEast();
					positions[4] = positions[4].south();
					positions[5] = positions[5].southWest();
					positions[6] = positions[6].west();
					positions[7] = positions[7].northWest();
				}
				return positions[p];
			}
		};
	}

	@Override
	public void validateMove(ValidatedMove vm, Engine engine) throws IllegalMove {
		validateContinuousMove(vm, (dCol, dRow) -> (Math.abs(dRow) == Math.abs(dCol) || (dRow * dCol == 0)),
				(dCol, dRow) -> true);
	}

	@Override
	public boolean canTake(Piece target, Board board) {
		int dc = target.getPosition().column - getPosition().column;
		int dr = target.getPosition().row - getPosition().row;
		if (!((dr != 0 && Math.abs(dr) == Math.abs(dc)) || (dr * dc == 0)))
			return false;
		dr = normaliseDirection(dr);
		dc = normaliseDirection(dc);
		return canTake(target, board, dc, dr);
	}

	@Override
	public double getRatingValue() {
		return 8;
	}

}
