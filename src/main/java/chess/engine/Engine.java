package chess.engine;

import chess.model.Board;
import chess.model.IllegalMove;
import chess.model.King;
import chess.model.Knight;
import chess.model.Pawn;
import chess.model.Piece;
import chess.model.Piece.Colour;
import chess.model.Position;
import chess.model.Queen;
import chess.model.Rook;

import static chess.model.Position.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import chess.model.Bishop;

public class Engine extends EngineSupport{

	public Engine() {
		this.validations = new Validations(this);
	}

	public ValidatedMove validateForPlayer(Move move) {
		ValidatedMove vm = validate(move);
		if (wouldPlayerBeCheckedIfHePlayedMove(vm))
			throw new IllegalMove(move.getPlayer()+" would be checked", move);
		return vm;
	}

	protected ValidatedMove validate(Move move) {
		ValidatedMove vm = validations.validateBasic(move, board, turn);
		if (vm.getMovingPiece() instanceof Pawn)
			validations.validatePawnMove(vm);
		else if (vm.getMovingPiece() instanceof Rook)
			validations.validateRookMove(vm);
		else if (vm.getMovingPiece() instanceof Knight)
			validations.validateKnightMove(vm);
		else if (vm.getMovingPiece() instanceof Bishop)
			validations.validateBishopMove(vm);
		else if (vm.getMovingPiece() instanceof Queen)
			validations.validateQueenMove(vm);
		else if (vm.getMovingPiece() instanceof King)
			validations.validateKingMove(vm);
		else
			throw new IllegalMove("Unknown move", move);
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
			return validate(move);
		} catch (IllegalMove e) {
			return null;
		}
	}

	public List<ValidatedMove> getValidMovesFor(Position position) {
		List<ValidatedMove> validMoves = new ArrayList<>();
		Piece piece = board.getPieceAt(position);
		if (piece != null) {
			List<Move> moves = piece.getPossibleMoves();
			validMoves = moves.stream().map(m -> isValid(m)).filter(m -> m != null).collect(Collectors.toList());
		}
		return validMoves;
	}

	public boolean isChecked(Colour player) {
		King king = getKingOf(player);
		return getPiecesOnBoard().stream().anyMatch((p) -> {
			if (p.getColour() != player) {
				List<ValidatedMove> moves = getValidMovesFor(p.getPosition());
				for (ValidatedMove vm : moves)
					if (vm.getTo() == king.getPosition())
						return true;
			}
			return false;
		});
	}

	public double getRating(Colour player) {
		//rating is [0,1] with 0 meaning he lost and 1 he won
		
		//get max 0.5 from pieces
		double rating = getPiecesOnBoard().stream().mapToDouble(p -> p.getColour() == player ? 1.0 : 0).sum()/32.0;
		
		//halve rating if checked
		if (isChecked(player))
			rating = rating*0.5;
		
		//take square root if other player is checked (since value is <1, square-rooting increases score)
		if (isChecked(getOpponentOf(player)))
			rating = Math.sqrt(rating);
		return rating;
	}

	protected SearchResult getBestMoveFor(Colour colour, int depth) {
		if (depth == maxDepth) {
			return new SearchResult(getRating(colour), null);
		}
		List<Piece> playerPieces = getPiecesOnBoard().stream().filter(p -> p.getColour() == colour)
				.collect(Collectors.toList());
		List<ValidatedMove> moves = playerPieces.stream().map(piece -> getValidMovesFor(piece.getPosition()))
				.flatMap(List::stream).collect(Collectors.toList());
		// TODO: what if list empty?
		ValidatedMove myMoveThatGivesTheOtherPlayerHisLowestScore = null;
		double bestScoreForOtherPlayer = 10000;
		for (ValidatedMove move : moves) {
			makeMove(move);
			if (!isChecked(move.getPlayer())) {
				SearchResult bestMoveForOtherPlayer = getBestMoveFor(getOpponentOf(colour), depth+1);
				if (bestMoveForOtherPlayer.rating < bestScoreForOtherPlayer) {
					bestScoreForOtherPlayer = bestMoveForOtherPlayer.rating;
					myMoveThatGivesTheOtherPlayerHisLowestScore = move;
				}
			}
			undoMove(move);
		}
		return new SearchResult(1.0-bestScoreForOtherPlayer, myMoveThatGivesTheOtherPlayerHisLowestScore);
	}

	public ValidatedMove getBestMoveFor(Colour colour) {
		return getBestMoveFor(colour, 0).move;
	}

}
