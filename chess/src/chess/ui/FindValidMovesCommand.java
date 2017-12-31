package chess.ui;

import java.util.List;
import java.util.stream.Collectors;

import chess.engine.BaseMove;
import chess.engine.Engine;
import chess.engine.Move;
import chess.model.Position;
import chess.model.Piece.Colour;

public class FindValidMovesCommand extends Command {

	Position position;

	public FindValidMovesCommand(Engine engine, Console console, String sPosition) {
		super(engine, console);
		this.position = Position.fromString(sPosition);
	}

	@Override
	public void execute() {
		List<Move> moves = engine.getValidMovesFor(position);
		List<Position> positions = moves.stream().map((move)->move.getTo()).collect(Collectors.toList());
		System.out.println(positions);
		console.printBoard();
		console.markPositions(positions);
		console.flush();
	}

}
