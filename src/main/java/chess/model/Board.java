package chess.model;

public class Board {

	protected Piece[][] board = new Piece[8][8];

	public Board(){
	}
	
	public Piece getPieceAt(Position p) {
		return board[p.column][p.row];
	}
	
	public void placePiece(Piece piece) {
		Position pos = piece.getPosition();
		board[pos.column][pos.row] = piece;
	}
	
	public void removePiece(Piece piece) {
		Position pos = piece.getPosition();
		board[pos.column][pos.row] = null;
	}
	
}
