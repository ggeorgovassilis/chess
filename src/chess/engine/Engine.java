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

public class Engine {

	final int maxDepth = 3;
	Board board;
	int turn;
	Validations validations;
	Map<Colour, King> kings = new HashMap<>();

	public Engine() {
		this.validations = new Validations(this);
	}

	public Board getBoard() {
		return board;
	}

	protected List<Piece> getPieces() {
		List<Piece> pieces = new ArrayList<Piece>(32);
		for (int column = COL(1); column <= COL(8); column++)
			for (int row = ROW(1); row <= ROW(8); row++) {
				Piece p = board.getPieceAt(Position.position(column, row));
				if (p != null)
					pieces.add(p);
			}
		return pieces;
	}

	public void setBoard(Board board) {
		this.board = board;
		kings.clear();
		getPieces().stream().forEach((piece) -> {
			if (piece instanceof King)
				kings.put(piece.getColour(), (King) piece);
		});
	}

	protected King getKingOf(Colour colour) {
		return kings.get(colour);
	}

	public ValidatedMove validate(Move move) {
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

	public ValidatedMove makeMove(ValidatedMove move) {
		validations.verifyThatMoveAppliesToThisBoard(move, board, turn);
		board.removePiece(move.movingPiece);
		move.movingPiece.setPosition(move.getTo());
		if (move.getCapturedPiece() != null)
			board.removePiece(move.capturedPiece);
		board.placePiece(move.movingPiece);
		turn++;
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
		turn--;
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
		return getPieces().stream().anyMatch((p) -> {
			if (p.getColour() != player) {
				List<ValidatedMove> moves = getValidMovesFor(p.getPosition());
				for (ValidatedMove vm : moves)
					if (vm.getTo() == king.getPosition())
						return true;
			}
			return false;
		});
	}

	protected Colour getOtherPlayer(Colour p) {
		return p == Colour.white ? Colour.black : Colour.white;
	}

	public double getRating(Colour player) {
		//rating is [0,1] with 0 meaning he lost and 1 he won
		
		//get max 0.5 from pieces
		double rating = getPieces().stream().mapToDouble(p -> p.getColour() == player ? 1.0 : 0).sum()/32.0;
		
		//halve rating if checked
		if (isChecked(player))
			rating = rating*0.5;
		
		//take square root if other player is checked (since value is <1, square-rooting increases score)
		if (isChecked(getOtherPlayer(player)))
			rating = Math.sqrt(rating);
		return rating;
	}

	public int getTurn() {
		return turn;
	}
	
	protected SearchResult getBestMoveFor(Colour colour, int depth) {
		if (depth == maxDepth) {
			return new SearchResult(getRating(colour), null);
		}
		List<Piece> playerPieces = getPieces().stream().filter(p -> p.getColour() == colour)
				.collect(Collectors.toList());
		List<ValidatedMove> moves = playerPieces.stream().map(piece -> getValidMovesFor(piece.getPosition()))
				.flatMap(List::stream).collect(Collectors.toList());
		// TODO: what if list empty?
		ValidatedMove myMoveThatGivesTheOtherPlayerHisLowestScore = moves.get(0);
		double bestScoreForOtherPlayer = 10000;
		for (ValidatedMove move : moves) {
			makeMove(move);
			SearchResult bestMoveForOtherPlayer = getBestMoveFor(getOtherPlayer(colour), depth+1);
			if (bestMoveForOtherPlayer.rating < bestScoreForOtherPlayer) {
				bestScoreForOtherPlayer = bestMoveForOtherPlayer.rating;
				myMoveThatGivesTheOtherPlayerHisLowestScore = move;
			}
			undoMove(move);
		}
		return new SearchResult(1.0-bestScoreForOtherPlayer, myMoveThatGivesTheOtherPlayerHisLowestScore);
	}

	public ValidatedMove getBestMoveFor(Colour colour) {
		return getBestMoveFor(colour, 0).move;
	}

}
