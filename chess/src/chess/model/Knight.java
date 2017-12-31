package chess.model;

public class Knight extends Piece {

	public Knight(Colour colour) {
		super(colour);
	}

	@Override
	public String getShortNotation() {
		return getColour() == Colour.white ? "N" : "n";
	}

	@Override
	public String getSymbol() {
		return getColour() == Colour.white ? "♘" : "♞";
	}

}
