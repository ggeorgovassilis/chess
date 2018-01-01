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
	public final Colour player;
	final Move move;

	public MoveCommand(Engine engine, Colour player, Console console, String from, String to) {
		super(engine, console);
		this.from = from;
		this.to = to;
		this.player = player;
		Position pFrom = Position.fromString(from);
		Position pTo = Position.fromString(to);
		this.move = new BaseMove(player, pFrom, pTo);
	}

	@Override
	public void execute() {
		ValidatedMove validMove = engine.validateForPlayer(this.move);
		engine.makeMove(validMove);
		console.setLastMove(validMove);
		console.printBoard();
		console.flush();
		console.println("Calculating move...");
		ValidatedMove responseMove = engine.getBestMoveFor(getOpponent(player));
		engine.makeMove(responseMove);
		console.printBoard();
		console.flush();
		console.println("Turn "+engine.getTurn());
		if (engine.isChecked(Colour.white))
			System.out.println("White checked");
		if (engine.isChecked(Colour.black))
			System.out.println("Black checked");
	}
}
