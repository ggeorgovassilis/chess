package chess.model;

import static chess.model.Position.ROW;

import java.util.Iterator;

import chess.engine.Engine;
import chess.engine.Move;
import chess.engine.ValidatedMove;
import chess.model.Piece.Colour;
import chess.model.Piece.MoveProducer;

public class Pawn extends Piece {

	public Pawn(Colour colour) {
		super(colour);
	}

	@Override
	public String getShortNotation() {
		return getColour() == Colour.white ? "P" : "p";
	}

	@Override
	public String getSymbol() {
		return getColour() == Colour.white ? "♙" : "♟";
	}

	@Override
	protected MoveProducer generateMoves() {
		return new MoveProducer() {

			@Override
			int getMaxMoveCounter() {
				return 4;
			}

			@Override
			Position getDestinationPosition(int moveCounter) {
				if (colour == Colour.white) {
					switch (moveCounter) {
					case 0:
						return position.north();
					case 1:
						return position.north().north();
					case 2:
						return position.northEast();
					case 3:
						return position.northWest();
					default:
						return null;
					}
				} else {
					switch (moveCounter) {
					case 0:
						return position.south();
					case 1:
						return position.south().south();
					case 2:
						return position.southEast();
					case 3:
						return position.southWest();
					default:
						return null;
					}
				}
			}
		};
	}

	@Override
	public void validateMove(ValidatedMove vm, Engine engine) throws IllegalMove {
		Position oneStepForward;
		Position twoStepsForward;
		Position captureMoveLeft;
		Position captureMoveRight;
		Position pawnBaseLine;
		Board board = vm.getBoard();
		if (!(vm.getMovingPiece() instanceof Pawn))
			throw new IllegalMove("Piece is not a pawn", vm);
		if (vm.getPlayer() == Colour.white) {
			oneStepForward = vm.getFrom().north();
			twoStepsForward = oneStepForward.north();
			captureMoveLeft = vm.getFrom().northWest();
			captureMoveRight = vm.getFrom().northEast();
			pawnBaseLine = Position.position(vm.getFrom().column, ROW(2));
		} else { // black
			oneStepForward = vm.getFrom().south();
			twoStepsForward = oneStepForward.south();
			captureMoveLeft = vm.getFrom().southWest();
			captureMoveRight = vm.getFrom().southWest();
			pawnBaseLine = Position.position(vm.getFrom().column, ROW(7));
		}
		// move forward to empty square is ok
		if (vm.getTo() == oneStepForward && board.getPieceAt(vm.getTo()) == null)
			return;
		// two steps forward from baseline to empty square is ok if first step is also
		// empty
		if (vm.getTo() == twoStepsForward && vm.getFrom() == pawnBaseLine && board.getPieceAt(vm.getTo()) == null && board.getPieceAt(oneStepForward) == null)
			return;
		// a side move is ok if there is a piece to capture;
		if ((vm.getTo() == captureMoveLeft || vm.getTo() == captureMoveRight) && vm.getCapturedPiece() != null)
			return;
		throw new IllegalMove("This is not a valid pawn move", vm);
	}

	@Override
	public boolean canTake(Piece p, Engine engine) {
		int dc = Math.abs(getPosition().column-p.getPosition().column);
		int dr = getPosition().row-p.getPosition().row;
		int direction = getColour()==Colour.white?1:-1;
		return dc*dr*direction==1;
	}

}
