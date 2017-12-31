package chess.model;

public class Queen extends Piece {

	public Queen(Colour colour) {
		super(colour);
	}

	@Override
	public String getShortNotation() {
		return getColour() == Colour.white ? "Q" : "q";
	}

	@Override
	public String getSymbol() {
		return getColour() == Colour.white ? "♕" : "♛";
	}

}
