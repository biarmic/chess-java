import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Position extends JLabel{
	private int row;
	private int column;
	public Position(int row, int column, ChessListener listener) throws IOException {
		setIcon(new ImageIcon(ImageIO.read(getClass().getResource("dot.png"))));
		setBounds(column*100+50,row*100+50,100,100);
		this.row = row;
		this.column = column;
		addMouseListener(listener);
	}
	public int getRow() {
		return row;
	}
	public int getColumn() {
		return column;
	}
}
