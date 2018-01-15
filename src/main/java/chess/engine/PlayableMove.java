package chess.engine;

/**
 * This move has been validated that it doesn't leave own's king checked
 *
 */
public class PlayableMove extends ValidatedMove{

	public PlayableMove(ValidatedMove vm) {
		super(vm, vm.getMovingPiece(), vm.getCapturedPiece(), vm.getBoard(), vm.getTurn());
	}

}
