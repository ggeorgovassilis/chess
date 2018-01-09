package chess.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chess.model.Piece.Colour;

public class Board {

	protected Piece[][] board = new Piece[8][8];
	protected Set<Piece> whitePieces = new HashSet<>();
	protected Set<Piece> blackPieces = new HashSet<>();

	public Board(){
	}

	public Set<Piece> getPiecesFor(Colour c){
		return c==Colour.white?whitePieces:blackPieces;
	}
	
	public Piece getPieceAt(Position p) {
		return board[p.column][p.row];
	}
	
	public void placePiece(Piece piece) {
		Position pos = piece.getPosition();
		board[pos.column][pos.row] = piece;
		getPiecesFor(piece.getColour()).add(piece);
	}
	
	public void removePiece(Piece piece) {
		Position pos = piece.getPosition();
		board[pos.column][pos.row] = null;
		getPiecesFor(piece.getColour()).remove(piece);
	}
	
}
