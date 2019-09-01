import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import javax.swing.JFrame;

public class GUI extends JFrame {
	private Board board;
	public GUI() {
		super("Chess");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(906,929);
		setResizable(false);
		setLocation((int)(screenSize.width-906)/2,(int)(screenSize.height-929)/2);
		board = new Board();
		add(board.getPanel());
		setVisible(true);
	}
	public static void main(String[] args) throws IOException {
		new GUI();
	}
}
