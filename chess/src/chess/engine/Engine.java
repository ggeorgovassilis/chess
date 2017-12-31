package chess.engine;

import chess.model.Board;
import chess.model.IllegalMove;
import chess.model.King;
import chess.model.Knight;
import chess.model.Pawn;
import chess.model.Piece;
import chess.model.Position;
import chess.model.Queen;
import chess.model.Rook;

import static chess.model.Position.*;

import java.util.ArrayList;
import java.util.List;

import chess.model.Bishop;

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
		else if (vm.getMovingPiece() instanceof Bishop)
			validations.validateBishopMove(vm);
		else if (vm.getMovingPiece() instanceof Queen)
			validations.validateQueenMove(vm);
		else if (vm.getMovingPiece() instanceof King)
			validations.validateKingMove(vm);
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
		turn++;
		return move;
	}
	
	public boolean isValid(Move move) {
		try {
			validate(move);
			return true;
		}
		catch (IllegalMove e) {
			return false;
		}
	}

	public List<Move> getValidMovesFor(Position position) {
		List<Move> moves = new ArrayList<>();
		Piece piece = board.getPieceAt(position);
		if (piece != null) {
			for (int col = COL(1); col <= COL(8); col++)
				for (int row = ROW(1); row <= ROW(8); row++) {
					Position from = piece.getPosition();
					Position to = Position.position(col, row);
					Move move = new BaseMove(piece.getColour(), from, to);
					if (isValid(move))
						moves.add(move);
				}
		}
		return moves;
	}
}
