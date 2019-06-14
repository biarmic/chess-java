import java.io.IOException;
import java.util.ArrayList;

public class Rook extends Piece{
	public Rook(boolean isWhite, int row, int column) throws IOException {
		super(isWhite,row,column,isWhite ? "rook_white.png" : "rook_black.png");
	}
}
