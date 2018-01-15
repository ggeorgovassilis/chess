package chess.model;

import static chess.model.Position.ROW;

import chess.engine.Engine;
import chess.engine.ValidatedMove;

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
		return new MoveProducer(getPosition()) {

			@Override
			int getMaxMoveCounter() {
				return 4;
			}

			@Override
			Position getDestinationPosition(int moveCounter) {
				if (colour == Colour.white) {
					switch (moveCounter) {
					case 0:
						return startingPosition.north();
					case 1:
						return startingPosition.north().north();
					case 2:
						return startingPosition.northEast();
					case 3:
						return startingPosition.northWest();
					default:
						return null;
					}
				} else {
					switch (moveCounter) {
					case 0:
						return startingPosition.south();
					case 1:
						return startingPosition.south().south();
					case 2:
						return startingPosition.southEast();
					case 3:
						return startingPosition.southWest();
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
		if (vm.getTo() == twoStepsForward && vm.getFrom() == pawnBaseLine && board.getPieceAt(vm.getTo()) == null
				&& board.getPieceAt(oneStepForward) == null)
			return;
		// a side move is ok if there is a piece to capture;
		if ((vm.getTo() == captureMoveLeft || vm.getTo() == captureMoveRight) && vm.getCapturedPiece() != null)
			return;
		throw new IllegalMove("This is not a valid pawn move", vm);
	}

	@Override
	public boolean canTake(Piece target, Board board) {
		int dc = Math.abs(getPosition().column - target.getPosition().column);
		int dr = target.getPosition().row - getPosition().row;
		int direction = getColour() == Colour.white ? 1 : -1;
		// Rationale: pawn can take piece only diagonally;
		// so must be dc=-1 or 1 and dr =-1 or 1
		// dc's sign doesn't matter, only dr's sign matters because of different
		// colour directions.
		// White moves up, so dr=1, black moves down, so dr=-1
		// For white to take: abs(dc)=1, dr=1, direction=1
		// For black to take: abs(dc)=1, dr=-1, direction=-1
		// thus abs(dc)*dr*direction == 1
		return dc * dr * direction == 1;
	}

	@Override
	public double getRatingValue() {
		return 1;
	}

}
