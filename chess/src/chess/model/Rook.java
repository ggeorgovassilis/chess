package chess.model;

public class Rook extends Piece {

	public Rook(Colour colour) {
		super(colour);
	}

	@Override
	public String getShortNotation() {
		return getColour() == Colour.white ? "R" : "r";
	}

	@Override
	public String getSymbol() {
		return getColour() == Colour.white ? "♖" : "♜";
	}

}
