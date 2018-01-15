package chess.model;

import chess.engine.Engine;
import chess.engine.ValidatedMove;

public class King extends Piece {

	public King(Colour colour) {
		super(colour);
	}

	@Override
	public String getShortNotation() {
		return getColour() == Colour.white ? "K" : "k";
	}

	@Override
	public String getSymbol() {
		return getColour() == Colour.white ? "♔" : "♚";
	}

	@Override
	protected MoveProducer generateMoves() {
		return new MoveProducer() {

			@Override
			int getMaxMoveCounter() {
				return 8;
			}

			@Override
			Position getDestinationPosition(int moveCounter) {
				Position p = getPosition();
				switch (moveCounter) {
				case 0:
					return p.north();
				case 1:
					return p.northEast();
				case 2:
					return p.east();
				case 3:
					return p.southEast();
				case 4:
					return p.south();
				case 5:
					return p.southWest();
				case 6:
					return p.west();
				case 7:
					return p.northWest();
				default:
					return null;
				}
			}
		};
	}

	@Override
	public void validateMove(ValidatedMove vm, Engine engine) throws IllegalMove {
		validateContinuousMove(vm, (dCol, dRow) -> Math.abs(dRow) * Math.abs(dCol) <= 1,
				(dCol, dRow) -> true);
	}

	@Override
	public boolean canTake(Piece target, Board board) {
		int dc = Math.abs(target.getPosition().column - getPosition().column);
		int dr = Math.abs(target.getPosition().row - getPosition().row);
		return dc<=1 && dr<=1;
	}

}
