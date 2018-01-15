package chess.engine;

import java.util.List;

import chess.model.Board;
import chess.model.Piece;
import chess.model.Piece.Colour;

public interface Engine {

	List<PlayableMove> getPlayableMovesFor(Piece piece);

	boolean isChecked(Colour player);

	PlayableMove getBestMoveFor(Colour colour);
	
	PlayableMove validateThatMoveIsPlayable(Move move);

	Board getBoard();
	
	void undoMove(ValidatedMove move);

	int getTurn();
	
	Piece getPieceThatChecksKing(Colour colour);
	
	PlayableMove playMove(PlayableMove move);

}