package chess.engine;

import chess.model.Bishop;
import chess.model.Board;
import chess.model.King;
import chess.model.Knight;
import chess.model.Pawn;
import chess.model.Piece;
import chess.model.Position;
import chess.model.Queen;
import chess.model.Rook;
import chess.model.Piece.Colour;

public class BoardFactory {

	interface PieceMaker {
		Piece make(Colour colour);
	}
	
	void setupPiece(PieceMaker m, Colour colour, int column, int row, Board board) {
		Piece piece = m.make(colour);
		piece.setPosition(Position.position(column, row));
		board.placePiece(piece);
	}


	public Board createStandardSetup() {
		Board board = new Board();
		// pawns
		for (int column = 0; column < 8; column++) {
			setupPiece((c) -> new Pawn(c), Colour.black, column, 6, board);
			setupPiece((c) -> new Pawn(c), Colour.white, column, 1, board);
		}
		// kings
		setupPiece((c) -> new King(c), Colour.black, 4, 7, board);
		setupPiece((c) -> new King(c), Colour.white, 4, 0, board);

		// queens
		setupPiece((c) -> new Queen(c), Colour.black, 3, 7, board);
		setupPiece((c) -> new Queen(c), Colour.white, 3, 0, board);

		// bishops
		setupPiece((c) -> new Bishop(c), Colour.black, 2, 7, board);
		setupPiece((c) -> new Bishop(c), Colour.black, 5, 7, board);

		setupPiece((c) -> new Bishop(c), Colour.white, 2, 0, board);
		setupPiece((c) -> new Bishop(c), Colour.white, 5, 0, board);

		// knights
		setupPiece((c) -> new Knight(c), Colour.black, 1, 7, board);
		setupPiece((c) -> new Knight(c), Colour.black, 6, 7, board);

		setupPiece((c) -> new Knight(c), Colour.white, 1, 0, board);
		setupPiece((c) -> new Knight(c), Colour.white, 6, 0, board);

		// rooks
		setupPiece((c) -> new Rook(c), Colour.black, 0, 7, board);
		setupPiece((c) -> new Rook(c), Colour.black, 7, 7, board);

		setupPiece((c) -> new Rook(c), Colour.white, 0, 0, board);
		setupPiece((c) -> new Rook(c), Colour.white, 7, 0, board);
		return board;
	}

	public Board createSetupForEasyMate() {
		Board board = new Board();
		// king
		setupPiece((c) -> new King(c), Colour.black, 4, 7, board);
		setupPiece((c) -> new King(c), Colour.white, 4, 0, board);

		setupPiece((c) -> new Rook(c), Colour.white, 0, 6, board);
		setupPiece((c) -> new Queen(c), Colour.white, 2, 6, board);
		return board;
	}
}
