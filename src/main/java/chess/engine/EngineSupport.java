package chess.engine;

import static chess.model.Position.COL;
import static chess.model.Position.ROW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chess.model.Board;
import chess.model.King;
import chess.model.Piece;
import chess.model.Position;
import chess.model.Piece.Colour;

abstract class EngineSupport {

	protected final int maxDepth = 3;
	protected Board board;
	protected int turn;
	protected Validations validations;
	protected Map<Colour, King> kings = new HashMap<>();
	
	protected EngineSupport() {
	}
	
	protected void cacheKings() {
		kings.clear();
		getPiecesOnBoard().stream().forEach((piece) -> {
			if (piece instanceof King)
				kings.put(piece.getColour(), (King) piece);
		});
	}

	public Board getBoard() {
		return board;
	}

	protected List<Piece> getPiecesOnBoard() {
		List<Piece> pieces = new ArrayList<Piece>(32);
		for (int column = COL(1); column <= COL(8); column++)
			for (int row = ROW(1); row <= ROW(8); row++) {
				Piece p = board.getPieceAt(Position.position(column, row));
				if (p != null)
					pieces.add(p);
			}
		return pieces;
	}
	
	public void setBoard(Board board) {
		this.board = board;
		cacheKings();
	}
	
	protected King getKingOf(Colour colour) {
		return kings.get(colour);
	}

	protected Colour getOpponentOf(Colour p) {
		return p == Colour.white ? Colour.black : Colour.white;
	}
	
	public int getTurn() {
		return turn;
	}

	protected void incrementTurn() {
		turn++;
	}

	protected void decreaseTurn() {
		turn--;
	}



}
