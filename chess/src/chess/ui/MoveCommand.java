package chess.ui;

import chess.engine.BaseMove;
import chess.engine.Engine;
import chess.engine.Move;
import chess.engine.ValidatedMove;
import chess.model.Piece.Colour;
import chess.model.Position;

public class MoveCommand extends Command{

	public final String from;
	public final String to;
	final Move move;

	public MoveCommand(Engine engine, Colour player, Console console, String from, String to) {
		super(engine, console);
		this.from = from;
		this.to = to;
		Position pFrom = Position.fromString(from);
		Position pTo = Position.fromString(to);
		this.move = new BaseMove(player, pFrom, pTo);
	}

	@Override
	public void execute() {
		ValidatedMove validMove = engine.validate(this.move);
		engine.makeMove(validMove);
		console.printBoard();
		console.flush();
	}
}
