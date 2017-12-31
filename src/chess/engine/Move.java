package chess.engine;

import chess.model.Piece;
import chess.model.Piece.Colour;
import chess.model.Position;

public interface Move {

	Position getFrom();
	Position getTo();
	Colour getPlayer();
}
