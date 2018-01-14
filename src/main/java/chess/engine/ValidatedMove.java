package chess.engine;

import chess.model.Board;
import chess.model.IllegalMove;
import chess.model.Piece;
import chess.model.Position;

public class ValidatedMove extends BaseMove {

	final Piece capturedPiece;
	final Piece movingPiece;
	final int turn;
	final Board board;

	public Piece getCapturedPiece() {
		return capturedPiece;
	}

	public Piece getMovingPiece() {
		return movingPiece;
	}

	public ValidatedMove(Move move, Piece movingPiece, Piece capturedPiece, Board board, int turn) {
		super(move.getPlayer(), move.getFrom(), move.getTo());
		this.capturedPiece = capturedPiece;
		this.movingPiece = movingPiece;
		this.board = board;
		this.turn = turn;
	}

	public int getTurn() {
		return turn;
	}

	public Board getBoard() {
		return board;
	}

	@Override
	public String toString() {
		if (capturedPiece == null)
			return getPlayer() + " " + getMovingPiece() + " " + getFrom() + "-" + getTo();
		else
			return getPlayer() + " " + getMovingPiece() + " " + getFrom() + "x" + getCapturedPiece()+" "+getTo();
	}
	
	public void validatePieceMoveRules(Engine engine) throws IllegalMove{
		getMovingPiece().validateMove(this, engine);
	}

}
