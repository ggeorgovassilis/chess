package chess.engine;

public class SearchResult {

	public final double rating;
	public final PlayableMove move;
	
	public SearchResult(double rating, PlayableMove move) {
		this.move = move;
		this.rating = rating;
	}
}
