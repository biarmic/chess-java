package gamestuff.pieces;
import gamestuff.PieceColor;
import gamestuff.PieceType;

public class Queen extends Piece {
	public Queen(PieceColor color,int row, int column) {
		super(PieceType.queen,color,row,column);
	}
}
