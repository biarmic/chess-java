package gamestuff;
import java.awt.Cursor;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import managers.DotListener;
import managers.PieceListener;
import windows.Board;

public class Dot extends JLabel {
	public static Board board;
	public static ImageIcon dot;
	public static ImageIcon corners;
	private Position position;
	public Dot(Position position, PieceListener source) {
		this.position = position;
		int row = position.getRow();
		int column = position.getColumn();
		if(board.findTile(row,column).getPieceType()==PieceType.empty)
			setIcon(dot);
		else
			setIcon(corners);
		setBounds(50+column*100,50+row*100,100,100);
		addMouseListener(new DotListener(position,source));
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	public Position getPosition() {
		return position;
	}
}
