package chess.engine;

import chess.model.Piece.Colour;
import chess.model.Position;

/**
 * The class hierarchy of moves:
 * {@link Move}: generic move. Might be speculative, not necessary a valid move. Always includes an existing
 * piece, but may point to an invalid location or not allowed by rules.
 * 
 * {@link ValidatedMove}: the move has been validated for proper positioning and that it is an allowed
 * move for the piece. Some expensive-to-compute corner cases have not been validated like
 * whether the move would leave the player's king checked.
 * 
 * {@link PlayableMove}: all validations have been executed; this move conforms to all rules
 * and can be played.
 *
 */
public interface Move {

	Position getFrom();
	Position getTo();
	Colour getPlayer();
}
