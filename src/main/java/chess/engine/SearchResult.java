package chess.engine;

public class SearchResult {

	public final double rating;
	public final ValidatedMove move;
	
	public SearchResult(double rating, ValidatedMove move) {
		this.move = move;
		this.rating = rating;
	}
}
