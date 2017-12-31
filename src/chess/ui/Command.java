package chess.ui;

import chess.engine.Engine;

public abstract class Command {

	protected Engine engine;
	protected Console console;
	
	protected Command(Engine engine, Console console) {
		this.engine = engine;
		this.console = console;
	}
	
	public abstract void execute();
}
