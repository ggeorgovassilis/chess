package chess.model;

public class Position {

	public final int row;
	public final int column;
	private Position north;
	private Position south;
	private Position east;
	private Position west;
	private Position northEast;
	private Position southEast;
	private Position southWest;
	private Position northWest;

	protected final static Position[][] positions = new Position[8][8];
	protected final static String[] cols = { "a", "b", "c", "d", "e", "f", "g", "h" };
	public final static Position illegalPosition = new Position(-1, -1);
	static {
		for (int column = 0; column < 8; column++)
			for (int row = 0; row < 8; row++)
				positions[column][row] = new Position(column, row);
		for (int column = 0; column < 8; column++)
			for (int row = 0; row < 8; row++) {
				Position p = positions[column][row];
				p.north = position(column, row + 1);
				p.south = position(column, row - 1);
				p.west = position(column - 1, row);
				p.east = position(column + 1, row);
				p.northEast = position(column + 1, row + 1);
				p.southEast = position(column + 1, row - 1);
				p.southWest = position(column - 1, row - 1);
				p.northWest = position(column - 1, row + 1);
			}
		illegalPosition.north = illegalPosition.south = illegalPosition.east = illegalPosition.west = illegalPosition.northEast = illegalPosition.southEast = illegalPosition.southWest = illegalPosition.northWest = illegalPosition;
	}

	private Position(int column, int row) {
		this.column = column;
		this.row = row;
	}

	@Override
	public String toString() {
		return cols[column] + (row + 1);
	}

	public static Position fromString(String s) {
		int column = s.charAt(0) - 'a';
		int row = s.charAt(1) - '1';
		return position(column, row);
	}

	public static Position position(int column, int row) {
		if (row < 0 || row > 7 || column < 0 || column > 7)
			return illegalPosition;
		return positions[column][row];
	}

	public Position north() {
		return north;
	}

	public Position south() {
		return south;
	}

	public Position east() {
		return east;
	}

	public Position west() {
		return west;
	}

	public Position northEast() {
		return northEast;
	}

	public Position southEast() {
		return southEast;
	}

	public Position southWest() {
		return southWest;
	}

	public Position northWest() {
		return northWest;
	}
	
	public static int ROW(int conventionalRow) {
		return conventionalRow - 1;
	}

	public static int COL(int conventionalColumn) {
		return conventionalColumn - 1;
	}


}
