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
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import chess.model.Bishop;
import chess.model.Board;

public class SimpleEngineImpl extends EngineSupport implements Engine {

	public SimpleEngineImpl(Board board) {
		setBoard(board);
		this.validations = new Validations(this);
	}

	@Override
	public PlayableMove validateThatMoveIsPlayable(Move move) {
		ValidatedMove vm = validatePieceMoves(move);
		if (wouldPlayerBeCheckedIfHePlayedMove(vm))
			throw new IllegalMove(move.getPlayer() + " would be checked", move);
		return new PlayableMove(vm);
	}

	protected ValidatedMove validateBasicBoardRules(Move move) {
		return validations.validateBasic(move, board, turn);
	}

	protected ValidatedMove validatePieceMoves(Move move) {
		ValidatedMove vm = validateBasicBoardRules(move);
		vm.validatePieceMoveRules(this);
		return vm;
	}

	public boolean wouldPlayerBeCheckedIfHePlayedMove(ValidatedMove move) {
		Colour player = move.getPlayer();
		makeMoveWithoutCheckingForCheck(move);
		boolean wouldMovingPlayerBeChecked = isChecked(player);
		undoMove(move);
		return wouldMovingPlayerBeChecked;
	}

	protected ValidatedMove makeMoveWithoutCheckingForCheck(ValidatedMove move) {
		validations.verifyThatMoveAppliesToThisBoard(move, board, turn);
		if (move.getCapturedPiece() != null)
			board.removePiece(move.capturedPiece);
		board.movePieceTo(move.movingPiece, move.getTo());
		move.movingPiece.setPosition(move.getTo());
		incrementTurn();
		return move;
	}

	@Override
	public PlayableMove playMove(PlayableMove move) {
		validations.verifyThatMoveAppliesToThisBoard(move, board, turn);
		if (move.getCapturedPiece() != null)
			board.removePiece(move.capturedPiece);
		board.movePieceTo(move.movingPiece, move.getTo());
		move.movingPiece.setPosition(move.getTo());
		incrementTurn();
		return move;
	}

	@Override
	public void undoMove(ValidatedMove move) {
		if (move.getTurn() != turn - 1)
			throw new RuntimeException("Wrong turn: " + move.getTurn() + " for " + move);
		if (move.getBoard() != board)
			throw new RuntimeException("Wrong board for move " + move);
		board.movePieceTo(move.getMovingPiece(), move.getFrom());
		if (move.getCapturedPiece() != null)
			board.placePiece(move.getCapturedPiece());
		move.getMovingPiece().setPosition(move.getFrom());
		decreaseTurn();
	}

	public ValidatedMove isValid(Move move) {
		try {
			return validatePieceMoves(move);
		} catch (IllegalMove e) {
			return null;
		}
	}

	private Iterator<ValidatedMove> getValidMovesFor(Piece piece) {
		Filterator<Move, ValidatedMove> filterator = new Filterator<>(piece.getPossibleMoves(),
				move -> isValid(move));
		return filterator;
	}

	@Override
	public List<PlayableMove> getPlayableMovesFor(Piece piece) {
		Stream<Move> stream = StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(piece.getPossibleMoves(), Spliterator.ORDERED), false);
		List<ValidatedMove> validatedMoves = stream.map(move -> isValid(move)).filter(vm -> vm != null)
				.collect(Collectors.toList());
		List<PlayableMove> playableMoves = new ArrayList<>();
		for (ValidatedMove vm : validatedMoves) {
			makeMoveWithoutCheckingForCheck(vm);
			boolean wouldPlayerBeCheked = isChecked(piece.getColour());
			if (!wouldPlayerBeCheked)
				playableMoves.add(new PlayableMove(vm));
			undoMove(vm);
		}
		return playableMoves;
	}

	@Override
	public boolean isChecked(Colour player) {
		final King king = getKingOf(player);
		return board.getPiecesFor(getOpponentOf(player)).stream().anyMatch(p -> p.canTake(king, getBoard()));
	}

	@Override
	public Piece getPieceThatChecksKing(Colour player) {
		final King king = getKingOf(player);
		List<Piece> checkers = board.getPiecesFor(getOpponentOf(player)).stream()
				.filter((p) -> p.canTake(king, getBoard())).collect(Collectors.toList());
		if (checkers.isEmpty())
			return null;
		return checkers.get(0);
	}

	protected double computeRatingFor(Colour player) {
		// IDEA: maintain two variables which store ratings for both players. update
		// them when pieces are captured.
		// rating is [0,1] with 0 meaning he lost and 1 he won
		// max score for pieces is 34, so: 1/34
		// and halve that because a pat would be 0.5, so max score from pieces
		// can't be better than a pat
		double rating = (1.0 / 68.0) * board.getPiecesFor(player).stream().mapToDouble(p -> p.getRatingValue()).sum();
		return rating;
	}

	protected SearchResult getBestMoveFor(Colour me, int depth, long endOfSearch) {
		if (depth == MAX_SEARCH_DEPTH || System.currentTimeMillis() > endOfSearch) {
			return new SearchResult(computeRatingFor(me), null);
		}
		Colour opponent = getOpponentOf(me);
		PlayableMove myMoveThatGivesOpponentHisLowestScore = null;
		// copy of pieces because MinMax will modify the piece set -> concurrent
		// modification exception
		List<Piece> pieces = new ArrayList<>(board.getPiecesFor(me));
		double bestScoreForOpponent = 10.0;
		for (Piece piece : pieces) {
			Iterator<ValidatedMove> myMoves = getValidMovesFor(piece);
			while (myMoves.hasNext()) {
				ValidatedMove move = myMoves.next();
				makeMoveWithoutCheckingForCheck(move);
				if (!isChecked(move.getPlayer())) {
					SearchResult bestMoveForOpponent = getBestMoveFor(opponent, depth + 1, endOfSearch);
					if (bestMoveForOpponent.rating < bestScoreForOpponent) {
						bestScoreForOpponent = bestMoveForOpponent.rating;
						myMoveThatGivesOpponentHisLowestScore = new PlayableMove(move);
					}
				}
				undoMove(move);
			}
		}
		if (myMoveThatGivesOpponentHisLowestScore == null) {
			bestScoreForOpponent = isChecked(me) ? 1.0 : 0.5;
		}
		return new SearchResult(1.0 - bestScoreForOpponent, myMoveThatGivesOpponentHisLowestScore);
	}

	@Override
	public PlayableMove getBestMoveFor(Colour colour) {
		return getBestMoveFor(colour, 0, System.currentTimeMillis() + MAX_SEARCH_TIME_MS).move;
	}

}
