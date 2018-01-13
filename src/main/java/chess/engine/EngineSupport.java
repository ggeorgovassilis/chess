package chess.engine;

import static chess.model.Position.COL;
import static chess.model.Position.ROW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chess.model.Board;
import chess.model.King;
import chess.model.Piece;
import chess.model.Position;
import chess.model.Piece.Colour;

abstract class EngineSupport {

	protected final int MAX_SEARCH_DEPTH = 5;
	protected final long MAX_SEARCH_TIME_MS = 20000; 
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

	public Board getBoard() {
		return board;
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
