public class Pawn extends Piece{
	public Pawn(boolean isWhite, int row, int column) {
		super(isWhite,row,column,isWhite ? "pawn_white.png" : "pawn_black.png");
	}
}
