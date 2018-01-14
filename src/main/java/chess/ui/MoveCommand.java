package chess.ui;

import chess.engine.BaseMove;
import chess.engine.Engine;
import chess.engine.Move;
import chess.engine.PlayableMove;
import chess.engine.ValidatedMove;
import chess.model.Piece.Colour;
import chess.model.Position;

public class MoveCommand extends Command {

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
		PlayableMove validMove = engine.validateForPlayer(this.move);
		engine.makeMove(validMove);
		console.setLastMove(validMove);
		console.drawBoard();
		console.drawPieces();
		console.flush();
		console.println("Calculating move...");
		long timestamp=-System.currentTimeMillis();
		PlayableMove responseMove = engine.getBestMoveFor(getOpponent(player));
		timestamp+=System.currentTimeMillis();
		if (responseMove != null) {
			console.println(responseMove.toString()+", "+timestamp+" ms");
			engine.makeMove(responseMove);
			console.drawBoard();
			console.drawPieces();
			console.flush();
		} else {
			console.println("Couldn't find move for oponent");
		}
		console.println("Turn " + engine.getTurn());
		if (engine.isChecked(Colour.white))
			System.out.println("White checked by "+engine.getPieceThatChecksKing(Colour.white)+" "+engine.getPieceThatChecksKing(Colour.white).getPosition());
		if (engine.isChecked(Colour.black))
			System.out.println("Black checked "+engine.getPieceThatChecksKing(Colour.black)+" "+engine.getPieceThatChecksKing(Colour.black).getPosition());
	}
}
