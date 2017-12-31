package chess.ui;

import chess.engine.Engine;

public class QuitCommand extends Command {
	public QuitCommand(Engine engine, Console console) {
		super(engine, console);
	}

	@Override
	public void execute() {
		console.println("Bye");
		console.close();
	}

}
