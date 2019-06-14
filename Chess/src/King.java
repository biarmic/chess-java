import java.io.IOException;
import java.util.ArrayList;

public class King extends Piece{
	public King(boolean isWhite, int row, int column) throws IOException {
		super(isWhite,row,column,isWhite ? "king_white.png" : "king_black.png");
	}
	public void doesCastle(int row, int column, boolean isWhite) {
		if(Math.abs(column-getColumn())==2) {
			int prevColumn = getColumn();
			int direction = column-prevColumn;
			Rook moving = (Rook) getPieces()[isWhite ? 0 : 1][direction<0 ? 8 : 9];
			moving.setPosition(row,column+(direction<0 ? 1 : -1));
		}
	}
}
