public class Rook extends Piece{
	public Rook(boolean isWhite, int row, int column) {
		super(isWhite,row,column,isWhite ? "rook_white.png" : "rook_black.png");
	}
}
