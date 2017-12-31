package chess.model;

import chess.engine.Move;

public class IllegalMove extends RuntimeException{

	Move move;
	
	public IllegalMove(String message, Move move) {
		super(message);
		this.move = move;
	}
	
	public Move getMove() {
		return move;
	}
}
