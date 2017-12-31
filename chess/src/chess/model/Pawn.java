package chess.model;

public class Pawn extends Piece{

	public Pawn(Colour colour) {
		super(colour);
	}

	@Override
	public String getShortNotation() {
		return getColour()==Colour.white?"P":"p";
	}

	@Override
	public String getSymbol() {
		return getColour()==Colour.white?"♙":"♟";
	}

}
