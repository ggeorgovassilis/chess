package chess.engine;

import java.util.HashMap;
import java.util.Map;

import chess.model.Board;
import chess.model.King;
import chess.model.Piece.Colour;

abstract class EngineSupport implements Engine {

	protected final int MAX_SEARCH_DEPTH = 5;
	protected final long MAX_SEARCH_TIME_MS = 3000000;
	protected Board board;
	protected int turn;
	protected Validations validations;
	protected Map<Colour, King> kings = new HashMap<>();

	protected EngineSupport() {
	}

	protected void cacheKings() {
		kings.clear();
		board.getPiecesFor(Colour.white).stream().forEach((piece) -> {
			if (piece instanceof King)
				kings.put(piece.getColour(), (King) piece);
		});
		board.getPiecesFor(Colour.black).stream().forEach((piece) -> {
			if (piece instanceof King)
				kings.put(piece.getColour(), (King) piece);
		});
	}

	@Override
	public Board getBoard() {
		return board;
	}

	protected void setBoard(Board board) {
		this.board = board;
		cacheKings();
	}

	protected King getKingOf(Colour colour) {
		return kings.get(colour);
	}

	protected Colour getOpponentOf(Colour p) {
		return p == Colour.white ? Colour.black : Colour.white;
	}

	@Override
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
