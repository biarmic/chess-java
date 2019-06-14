import java.io.IOException;
import java.util.ArrayList;

public class Queen extends Piece{
	public Queen(boolean isWhite, int row, int column) throws IOException {
		super(isWhite,row,column,isWhite ? "queen_white.png" : "queen_black.png");
	}
}
