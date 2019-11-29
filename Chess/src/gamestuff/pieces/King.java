package gamestuff.pieces;

import gamestuff.PieceColor;
import gamestuff.PieceType;

public class King extends Piece {
	public King(PieceColor color, int row, int column) {
		super(PieceType.king, color, row, column);
	}
}
