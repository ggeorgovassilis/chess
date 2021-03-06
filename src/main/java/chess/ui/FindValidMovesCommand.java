package chess.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import chess.engine.Engine;
import chess.engine.PlayableMove;
import chess.engine.ValidatedMove;
import chess.model.Position;
import chess.model.Piece;

public class FindValidMovesCommand extends Command {

	Position position;

	public FindValidMovesCommand(Engine engine, Console console, String sPosition) {
		super(engine, console);
		this.position = Position.fromString(sPosition);
	}
	
	List<ValidatedMove> toList(Iterator<ValidatedMove> ite){
		List<ValidatedMove> moves = new ArrayList<>();
		ite.forEachRemaining(moves::add);
		return moves;
	}

	@Override
	public void execute() {
		Piece piece = engine.getBoard().getPieceAt(position);
		List<PlayableMove> moves = piece!=null?engine.getPlayableMovesFor(piece):new ArrayList<>();
		List<Position> positions = moves.stream().map((move) -> move.getTo()).collect(Collectors.toList());
		System.out.println(positions);
		console.drawBoard();
		console.markPositions(positions);
		console.drawPieces();
		console.flush();
	}

}
