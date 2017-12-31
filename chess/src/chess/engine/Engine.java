package chess.engine;

import chess.model.Board;
import chess.model.IllegalMove;
import chess.model.Knight;
import chess.model.Pawn;
import chess.model.Rook;

import static chess.model.Position.*;

public class Engine {

	Board board;
	int turn;
	Validations validations;

	public Engine() {
		this.validations = new Validations(this);
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public ValidatedMove validate(Move move) {
		ValidatedMove vm = validations.validateBasic(move, board, turn);
		if (vm.getMovingPiece() instanceof Pawn)
			validations.validatePawnMove(vm);
		else if (vm.getMovingPiece() instanceof Rook)
			validations.validateRookMove(vm);
		else if (vm.getMovingPiece() instanceof Knight)
			validations.validateKnightMove(vm);
		else
			throw new IllegalMove("Unknown move", move);
		return vm;
	}

	public ValidatedMove makeMove(ValidatedMove move) {
		validations.verifyThatMoveAppliesToThisBoard(move, board, turn);
		board.removePiece(move.movingPiece);
		move.movingPiece.setPosition(move.getTo());
		if (move.getCapturedPiece() != null)
			board.removePiece(move.capturedPiece);
		board.placePiece(move.movingPiece);
		return move;
	}
}
