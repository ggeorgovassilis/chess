package chess.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import chess.engine.BaseMove;
import chess.engine.Engine;
import chess.engine.Move;
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
		return new MoveProducer() {

			@Override
			int getMaxMoveCounter() {
				return 24;
			}

			@Override
			Position getDestinationPosition(int moveCounter) {
				Position p = getPosition();
				int direction = moveCounter % 4;
				int dColumn = direction % 2 == 0 ? -1 : 1;
				int dRow = direction < 2 ? -1 : 1;
				int radius = moveCounter / 4;
				int column = p.column;
				int row = p.row;
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
	public boolean canTake(Piece p, Engine engine) {
		int dc = p.getPosition().column - getPosition().column;
		int dr = p.getPosition().row - getPosition().row;
		if (Math.abs(dc) != Math.abs(dr))
			return false;
		dc = dc > 0 ? 1 : -1;
		dr = dr > 0 ? 1 : -1;
		Position pos = getPosition();
		while (true) {
			pos = Position.position(pos.column + dc, pos.row + dr);
			Piece piece = engine.getBoard().getPieceAt(pos);
			if (piece == p)
				return true;
			if (piece != null)
				return false;
		}
	}

}
