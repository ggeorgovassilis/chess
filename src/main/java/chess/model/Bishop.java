package chess.model;

import chess.engine.Engine;
import chess.engine.ValidatedMove;

public class Bishop extends Piece {

	public Bishop(Colour colour) {
		super(colour);
	}

	@Override
	public String getShortNotation() {
		return getColour() == Colour.white ? "B" : "b";
	}

	@Override
	public String getSymbol() {
		return getColour() == Colour.white ? "♗" : "♝";
	}

	@Override
	protected MoveProducer generateMoves() {
		return new MoveProducer(getPosition()) {

			@Override
			int getMaxMoveCounter() {
				return 24;
			}

			@Override
			Position getDestinationPosition(int moveCounter) {
				int direction = moveCounter % 4;
				int dColumn = direction % 2 == 0 ? -1 : 1;
				int dRow = direction < 2 ? -1 : 1;
				int radius = moveCounter / 4;
				int column = startingPosition.column;
				int row = startingPosition.row;
				column += radius * dColumn;
				row += radius * dRow;
				return Position.position(column, row);
			}

		};
	}

	@Override
	public void validateMove(ValidatedMove vm, Engine engine) throws IllegalMove {
		validateContinuousMove(vm, (dCol, dRow) -> Math.abs(dRow) == Math.abs(dCol), (dCol, dRow) -> dRow * dCol != 0);
	}

	@Override
	public boolean canTake(Piece target, Board board) {
		int dc = target.getPosition().column - getPosition().column;
		int dr = target.getPosition().row - getPosition().row;
		if (Math.abs(dc) != Math.abs(dr))
			return false;
		dc = normaliseDirection(dc);
		dr = normaliseDirection(dr);
		return canTake(target, board, dc, dr);
	}

	@Override
	public double getRatingValue() {
		return 3;
	}

}
