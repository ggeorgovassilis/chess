package chess.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import chess.engine.BaseMove;
import chess.engine.Engine;
import chess.engine.Move;
import chess.engine.ValidatedMove;
import chess.model.Piece.MoveProducer;

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
					return p.north().northWest();
				case 1:
					return p.north().northEast();
				case 2:
					return p.east().northEast();
				case 3:
					return p.east().southEast();
				case 4:
					return p.south().southEast();
				case 5:
					return p.south().southWest();
				case 6:
					return p.west().southWest();
				case 7:
					return p.west().northWest();
				default:
					return null;
				}
			}
		};

	}

	@Override
	public void validateMove(ValidatedMove vm, Engine engine) throws IllegalMove {
		if (!(vm.getMovingPiece() instanceof Knight))
			throw new IllegalMove("Piece is not a knight", vm);
		int dRow = vm.getTo().row - vm.getFrom().row;
		int dCol = vm.getTo().column - vm.getFrom().column;
		if (Math.abs(dRow * dCol) != 2)
			throw new IllegalMove("This is not a valid knight move", vm);
	}

	@Override
	public boolean canTake(Piece p, Engine engine) {
		int dc = Math.abs(p.getPosition().column-getPosition().column);
		int dr = Math.abs(p.getPosition().row-getPosition().row);
		return (dr*dc==2);
	}

}
