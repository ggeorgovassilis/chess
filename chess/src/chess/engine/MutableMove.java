package chess.engine;

import chess.model.Piece;
import chess.model.Position;

public interface MutableMove extends Move{

	void setMovingPiece(Piece piece);
	void setFrom(Position from);
	void setTo(Position to);
	void setCapturedPiece(Piece piece);
}
