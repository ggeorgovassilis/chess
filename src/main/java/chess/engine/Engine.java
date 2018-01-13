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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import chess.model.Bishop;

public class Engine extends EngineSupport {

	Map<Class, Double> pieceRatings = new HashMap<>();

	public Engine() {
		this.validations = new Validations(this);
		pieceRatings.put(Pawn.class, 1.0);
		pieceRatings.put(Knight.class, 3.0);
		pieceRatings.put(Bishop.class, 4.0);
		pieceRatings.put(Rook.class, 5.0);
		pieceRatings.put(Queen.class, 6.0);
		pieceRatings.put(King.class, 0.0);
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

	public Iterator<ValidatedMove> getValidMovesFor(Piece piece) {
		FilterIterator<Move, ValidatedMove> filterator = new FilterIterator<>(piece.getPossibleMoves(),
				move -> isValid(move));
		return filterator;
	}

	public boolean isChecked(Colour player) {
		final King king = getKingOf(player);
		return board.getPiecesFor(getOpponentOf(player)).stream().anyMatch((p) -> {
			boolean b = p.canTake(king, this);
			return b;
		});
	}

	public double getRating(Colour player) {
		// rating is [0,1] with 0 meaning he lost and 1 he won

		double rating = board.getPiecesFor(player).stream().map(p -> pieceRatings.get(p.getClass()))
				.mapToDouble(Double::doubleValue).sum();

		// halve rating if checked
		if (isChecked(player))
			rating = rating * 0.9;
		rating=rating*0.1; //account for various pawn promotions

		// take square root if other player is checked (since value is <1,
		// square-rooting increases score)
		if (isChecked(getOpponentOf(player)))
			rating = Math.sqrt(rating);
		return rating;
	}

	protected SearchResult getBestMoveFor(Colour colour, int depth, long endOfSearch) {
		if (depth == MAX_SEARCH_DEPTH || System.currentTimeMillis()>endOfSearch) {
			return new SearchResult(getRating(colour), null);
		}
		ValidatedMove myMoveThatGivesTheOtherPlayerHisLowestScore = null;
		// copy of pieces because MinMax will modify the piece set -> concurrent
		// modification exception
		List<Piece> pieces = new ArrayList<>(board.getPiecesFor(colour));
		double bestScoreForOtherPlayer = 10000;
		for (Piece piece : pieces) {
			Iterator<ValidatedMove> moves = getValidMovesFor(piece);
			if (moves.hasNext())
				for (ValidatedMove move = moves.next(); moves.hasNext(); move = moves.next()) {
					makeMove(move);
					if (!isChecked(move.getPlayer())) {
						SearchResult bestMoveForOtherPlayer = getBestMoveFor(getOpponentOf(colour), depth + 1, endOfSearch);
						if (bestMoveForOtherPlayer.rating < bestScoreForOtherPlayer) {
							bestScoreForOtherPlayer = bestMoveForOtherPlayer.rating;
							myMoveThatGivesTheOtherPlayerHisLowestScore = move;
						}
					}
					undoMove(move);
				}
		}
		return new SearchResult(1.0 - bestScoreForOtherPlayer, myMoveThatGivesTheOtherPlayerHisLowestScore);
	}

	public ValidatedMove getBestMoveFor(Colour colour) {
		return getBestMoveFor(colour, 0, System.currentTimeMillis()+MAX_SEARCH_TIME_MS).move;
	}

}
