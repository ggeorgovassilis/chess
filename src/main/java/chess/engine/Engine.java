package chess.engine;

import chess.model.IllegalMove;
import chess.model.King;
import chess.model.Knight;
import chess.model.Pawn;
import chess.model.Piece;
import chess.model.Piece.Colour;
import chess.model.Position;
import chess.model.Queen;
import chess.model.Rook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import chess.model.Bishop;

public class Engine extends EngineSupport {

	public Engine() {
		this.validations = new Validations(this);
	}

	public ValidatedMove validateForPlayer(Move move) {
		ValidatedMove vm = validatePieceMoves(move);
		if (wouldPlayerBeCheckedIfHePlayedMove(vm))
			throw new IllegalMove(move.getPlayer() + " would be checked", move);
		return vm;
	}

	protected ValidatedMove validateBasicBoardRules(Move move) {
		return validations.validateBasic(move, board, turn);
	}

	protected ValidatedMove validatePieceMoves(Move move) {
		ValidatedMove vm = validateBasicBoardRules(move);
		vm.validateMore(this);
		return vm;
	}

	public boolean wouldPlayerBeCheckedIfHePlayedMove(ValidatedMove move) {
		Colour player = move.getPlayer();
		makeMove(move);
		boolean wouldMovingPlayerBeChecked = isChecked(player);
		undoMove(move);
		return wouldMovingPlayerBeChecked;
	}

	public ValidatedMove makeMove(ValidatedMove move) {
		validations.verifyThatMoveAppliesToThisBoard(move, board, turn);
		board.removePiece(move.movingPiece);
		move.movingPiece.setPosition(move.getTo());
		if (move.getCapturedPiece() != null)
			board.removePiece(move.capturedPiece);
		board.placePiece(move.movingPiece);
		incrementTurn();
		return move;
	}

	public void undoMove(ValidatedMove move) {
		if (move.getTurn() != turn - 1)
			throw new RuntimeException("Wrong turn: " + move.getTurn() + " for " + move);
		if (move.getBoard() != board)
			throw new RuntimeException("Wrong board for move " + move);
		board.removePiece(move.getMovingPiece());
		if (move.getCapturedPiece() != null)
			board.placePiece(move.getCapturedPiece());
		move.getMovingPiece().setPosition(move.getFrom());
		board.placePiece(move.getMovingPiece());
		decreaseTurn();
	}

	public ValidatedMove isValid(Move move) {
		try {
			return validatePieceMoves(move);
		} catch (IllegalMove e) {
			return null;
		}
	}

	public List<ValidatedMove> getValidMovesFor(Position position) {
		List<ValidatedMove> validMoves = new ArrayList<>();
		Piece piece = board.getPieceAt(position);
		if (piece != null) {
			piece.getPossibleMoves().forEachRemaining(m -> {
				ValidatedMove vm = isValid(m);
				if (vm != null)
					validMoves.add(vm);
			});
		}
		return validMoves;
	}

	public boolean isChecked(Colour player) {
		// IDEA: start reverse moves at the king's position; where they hit opponent
		// pieces, check if those pieces
		// check the king
		final Position kingPosition = getKingOf(player).getPosition();
		return board.getPiecesFor(getOpponentOf(player)).parallelStream().anyMatch((p) -> {
			List<ValidatedMove> moves = getValidMovesFor(p.getPosition());
			return moves.stream().anyMatch(vm -> vm.getTo() == kingPosition);
		});
	}

	public double getRating(Colour player) {
		// rating is [0,1] with 0 meaning he lost and 1 he won

		// get max 0.5 from pieces
		double rating = (double)board.getPiecesFor(player).size()/16.0;

		// halve rating if checked
		if (isChecked(player))
			rating = rating * 0.5;

		// take square root if other player is checked (since value is <1,
		// square-rooting increases score)
		if (isChecked(getOpponentOf(player)))
			rating = Math.sqrt(rating);
		return rating;
	}

	protected SearchResult getBestMoveFor(Colour colour, int depth) {
		if (depth == maxDepth) {
			return new SearchResult(getRating(colour), null);
		}
		ValidatedMove myMoveThatGivesTheOtherPlayerHisLowestScore = null;
		List<ValidatedMove> moves = board.getPiecesFor(colour).parallelStream()
				.map(piece -> getValidMovesFor(piece.getPosition())).flatMap(List::stream).collect(Collectors.toList());
		// TODO: what if list empty?
		double bestScoreForOtherPlayer = 10000;
		for (ValidatedMove move : moves) {
			makeMove(move);
			if (!isChecked(move.getPlayer())) {
				SearchResult bestMoveForOtherPlayer = getBestMoveFor(getOpponentOf(colour), depth + 1);
				if (bestMoveForOtherPlayer.rating < bestScoreForOtherPlayer) {
					bestScoreForOtherPlayer = bestMoveForOtherPlayer.rating;
					myMoveThatGivesTheOtherPlayerHisLowestScore = move;
				}
			}
			undoMove(move);
		}
		return new SearchResult(1.0 - bestScoreForOtherPlayer, myMoveThatGivesTheOtherPlayerHisLowestScore);
	}

	public ValidatedMove getBestMoveFor(Colour colour) {
		return getBestMoveFor(colour, 0).move;
	}

}
