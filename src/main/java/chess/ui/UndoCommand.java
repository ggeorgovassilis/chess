package chess.ui;

import chess.engine.Engine;
import chess.engine.ValidatedMove;
import chess.model.Piece.Colour;

public class UndoCommand extends Command{

	public UndoCommand(Engine engine, Console console) {
		super(engine, console);
	}

	@Override
	public void execute() {
		ValidatedMove lastMove = console.getLastMove();
		if (lastMove==null) {
			console.error("No last move");
			return;
		}
		console.setLastMove(null);
		engine.undoMove(lastMove);
		console.drawBoard();
		console.drawPieces();
		console.flush();
		console.println("Turn "+engine.getTurn());
		if (engine.isChecked(Colour.white))
			System.out.println("White checked");
		if (engine.isChecked(Colour.black))
			System.out.println("Black checked");
	}
}
