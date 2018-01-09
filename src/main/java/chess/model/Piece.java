package chess.model;

import java.io.ObjectInputStream.GetField;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;

import chess.engine.BaseMove;
import chess.engine.Engine;
import chess.engine.Move;
import chess.engine.ValidatedMove;

public abstract class Piece {

	abstract class MoveProducer implements Iterator<Move>{

		int moveCounter = 0;
		Move nextMove=null;
		abstract int getMaxMoveCounter();
		abstract Position getDestinationPosition(int moveCounter);

		protected Move getNextMove() {
			while (moveCounter<getMaxMoveCounter()) {
				Position p = getDestinationPosition(moveCounter);
				moveCounter++;
				if (p==null)
					throw new RuntimeException("Not a move");
				if (p!=Position.illegalPosition)
					return new BaseMove(colour, getPosition(), p);
			}
			return null;
		}
		
		protected void initialise() {
		};
		
		public void postInitialise() {
			initialise();
			nextMove = getNextMove();
		}
		

		@Override
		public boolean hasNext() {
			return nextMove!=null;
		}

		@Override
		public Move next() {
			Move move = nextMove;
			nextMove = getNextMove();
			return move;
		}
	};
	
	public enum Colour {
		black, white
	};

	protected final Colour colour;
	protected Position position;

	
	
	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public abstract String getShortNotation();
	public abstract String getSymbol();

	protected Piece(Colour colour) {
		this.colour = colour;
	}

	public Colour getColour() {
		return colour;
	}
	
	@Override
	public String toString() {
		return getShortNotation();
	}
	
	protected abstract MoveProducer generateMoves();
	
	public Iterator<Move> getPossibleMoves(){
		MoveProducer mp = generateMoves();
		mp.postInitialise();
		return mp;
	}
	
	protected void validateContinuousMove(ValidatedMove vm,
			BiPredicate<Integer, Integer> moveValidator, BiPredicate<Integer, Integer> normalisedMoveValidator) {
		if (!(vm.getMovingPiece()!=this)) {
			throw new IllegalMove("Move doesn't apply to this piece", vm);
		}
		Board board = vm.getBoard();
		int dRow = vm.getTo().row - vm.getFrom().row;
		int dCol = vm.getTo().column - vm.getFrom().column;
		if (!moveValidator.test(dCol, dRow))
			throw new IllegalMove("This isn't a valid move", vm);
		if (dRow < 0)
			dRow = -1;
		if (dRow > 0)
			dRow = 1;
		if (dCol < 0)
			dCol = -1;
		if (dCol > 0)
			dCol = 1;
		if (!normalisedMoveValidator.test(dCol, dRow))
			throw new IllegalMove("This isn't a valid move", vm);
		Position next = vm.getFrom();
		while (vm.getTo() != (next = Position.position(next.column + dCol, next.row + dRow))) {
			if (board.getPieceAt(next) != null)
				throw new IllegalMove("Movement obstructed at " + next, vm);
		}
		if (next != vm.getTo())
			throw new IllegalMove("This is not a valid rook move", vm);
	}

	
	public abstract void validateMove(ValidatedMove vm, Engine engine) throws IllegalMove;
}
