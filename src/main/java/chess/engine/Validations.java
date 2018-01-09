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
import chess.model.Piece.Colour;
import static chess.model.Position.*;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.IntBinaryOperator;
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;

import chess.model.Bishop;

public class Validations {

	protected Engine engine;

	public Validations(Engine engine) {
		this.engine = engine;
	}

	public ValidatedMove validateBasic(Move move, Board board, int turn) {
		if (move == null)
			throw new NullPointerException("Move");
		if (board == null)
			throw new NullPointerException("Board");
		Piece movingPiece = board.getPieceAt(move.getFrom());
		// is there a piece at the move starting position?
		if (movingPiece == null)
			throw new IllegalMove("There is no piece at " + move.getTo(), move);
		// is the start and end position coded into the board?
		if (move.getFrom() == Position.illegalPosition || move.getTo() == Position.illegalPosition)
			throw new IllegalMove("IllegalPosition", move);
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

}
