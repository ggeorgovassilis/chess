package chess.model;

import java.util.ArrayList;
import java.util.List;

import chess.engine.BaseMove;
import chess.engine.Move;

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

	@Override
	public List<Move> getPossibleMoves() {
		List<Move> moves = new ArrayList<>();
		if (colour == Colour.white) {
			moves.add(new BaseMove(colour, position, position.north())); 
			moves.add(new BaseMove(colour, position, position.north().north())); 
			moves.add(new BaseMove(colour, position, position.northEast())); 
			moves.add(new BaseMove(colour, position, position.northWest())); 
		} else {
			moves.add(new BaseMove(colour, position, position.south())); 
			moves.add(new BaseMove(colour, position, position.south().south())); 
			moves.add(new BaseMove(colour, position, position.southEast())); 
			moves.add(new BaseMove(colour, position, position.southWest())); 
		}
		return moves;
	}

}
