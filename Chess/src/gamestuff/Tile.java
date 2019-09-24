package gamestuff;

public class Tile {
	private PieceType type = PieceType.empty;
	private PieceColor color = PieceColor.empty;
	private int row;
	private int column;
	public Tile(int row, int column) {
		this.row = row;
		this.column = column;
	}
	public PieceType getPieceType() {
		return type;
	}
	public PieceColor getColor() {
		return color;
	}
	public int getRow() {
		return row;
	}
	public int getColumn() {
		return column;
	}
	public PieceType placePiece(PieceType type, PieceColor color) {
		PieceType previousType = this.type;
		this.type = type;
		this.color = color;
		return previousType;
	}
	public void removePiece() {
		type = PieceType.empty;
		color = PieceColor.empty;
	}
}
