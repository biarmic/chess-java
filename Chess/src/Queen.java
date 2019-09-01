public class Queen extends Piece{
	public Queen(boolean isWhite, int row, int column) {
		super(isWhite,row,column,isWhite ? "queen_white.png" : "queen_black.png");
	}
}
