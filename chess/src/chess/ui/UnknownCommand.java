package chess.ui;

import chess.engine.Engine;

public class UnknownCommand extends Command{

	public UnknownCommand(Engine engine, Console console) {
		super(engine, console);
	}
	
	@Override
	public void execute() {
		console.println("Unknown command");
	}

}
