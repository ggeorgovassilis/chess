package chess.engine;

import chess.model.Board;
import chess.model.IllegalMove;
import chess.model.Knight;
import chess.model.Pawn;
import chess.model.Piece;
import chess.model.Position;
import chess.model.Rook;
import chess.model.Piece.Colour;
import static chess.model.Position.*;

public class Validations {

	protected Engine engine;

	public Validations(Engine engine) {
		this.engine = engine;
	}

	public ValidatedMove validateBasic(Move move, Board board, int turn) {
		Piece movingPiece = board.getPieceAt(move.getFrom());
		// is there a piece at the move starting position?
		if (movingPiece == null)
			throw new IllegalMove("There is no piece at " + move.getTo(), move);
		// is the start and end position coded into the board?
		if (move.getFrom() == Position.illegalPosition || move.getTo() == Position.illegalPosition)
			throw new RuntimeException("IllegalPosition");
		// is there a piece of the moving player at the starting position?
		if (movingPiece.getColour() != move.getPlayer())
			throw new IllegalMove("Player " + move.getPlayer() + " not allowed to move other player's piece", move);
		// is this a move, at all?
		if (move.getFrom() == move.getTo())
			throw new IllegalMove("Start and end positions are the same", move);
		Piece capturedPiece = board.getPieceAt(move.getTo());
		// if there is a captured piece, does it belong to the opponent?
		if (capturedPiece != null && capturedPiece.getColour() == movingPiece.getColour())
			throw new IllegalMove("There is a piece of the same colour already at " + move.getTo(), move);
		ValidatedMove vm = new ValidatedMove(move, movingPiece, capturedPiece, board, turn);
		return vm;
	}

	public void verifyThatMoveAppliesToThisBoard(ValidatedMove move, Board board, int turn) {
		if (move.getBoard() != board)
			throw new RuntimeException("Move wasn't validated for this board");
		if (move.getTurn() != turn)
			throw new RuntimeException("Move was validated for a different turn");
	}

	public void validatePawnMove(ValidatedMove vm) {
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
		// two steps forward from baseline to empty square is ok
		if (vm.getTo() == twoStepsForward && vm.getFrom() == pawnBaseLine && board.getPieceAt(vm.getTo()) == null)
			return;
		// a side move is ok if there is a piece to capture;
		if ((vm.getTo() == captureMoveLeft || vm.getTo() == captureMoveRight) && vm.getCapturedPiece() != null)
			return;
		throw new IllegalMove("This is not a valid pawn move", vm);
	}

	public void validateRookMove(ValidatedMove vm) {
		Board board = vm.getBoard();
		if (!(vm.getMovingPiece() instanceof Rook))
			throw new IllegalMove("Piece is not a rook", vm);
		int dRow = vm.getTo().row - vm.getFrom().row;
		int dCol = vm.getTo().column - vm.getFrom().column;
		if (dRow < 0)
			dRow = -1;
		if (dRow > 0)
			dRow = 1;
		if (dCol < 0)
			dCol = -1;
		if (dCol > 0)
			dCol = 1;
		if (dRow * dCol != 0)
			throw new IllegalMove("Doesn't move in a single column or row", vm);
		Position next = vm.getFrom();
		while (vm.getTo() != (next = Position.position(next.column + dCol, next.row + dRow))) {
			if (board.getPieceAt(next) != null)
				throw new IllegalMove("Movement obstructed at " + next, vm);
		}
		if (next != vm.getTo())
			throw new IllegalMove("This is not a valid rook move", vm);
	}
	
	public void validateKnightMove(ValidatedMove vm) {
		if (!(vm.getMovingPiece() instanceof Knight))
			throw new IllegalMove("Piece is not a knight", vm);
		int dRow = vm.getTo().row - vm.getFrom().row;
		int dCol = vm.getTo().column - vm.getFrom().column;
		if (Math.abs(dRow*dCol)!=2)
			throw new IllegalMove("This is not a valid knight move", vm);
	}

}
