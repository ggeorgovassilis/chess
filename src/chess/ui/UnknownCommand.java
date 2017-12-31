package chess.ui;

import chess.engine.Engine;

public class UnknownCommand extends Command{

	public UnknownCommand(Engine engine, Console console) {
		super(engine, console);
	}
	
	@Override
	public void execute() {
		console.println("Example of valid commands:");
		console.println("b2b4");
		console.println("?b2");
		console.println("q");
	}

}
