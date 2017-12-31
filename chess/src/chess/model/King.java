package chess.model;

public class King extends Piece {

	public King(Colour colour) {
		super(colour);
	}

	@Override
	public String getShortNotation() {
		return getColour() == Colour.white ? "K" : "k";
	}

	@Override
	public String getSymbol() {
		return getColour() == Colour.white ? "♔" : "♚";
	}

}
