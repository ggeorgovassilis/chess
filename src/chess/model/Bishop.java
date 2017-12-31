package chess.model;

public class Bishop extends Piece{

	public Bishop(Colour colour) {
		super(colour);
	}

	@Override
	public String getShortNotation() {
		return getColour()==Colour.white?"B":"b";
	}

	@Override
	public String getSymbol() {
		return getColour()==Colour.white?"♗":"♝";
	}

}
