import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import javax.swing.JFrame;

public class GUI {
	private JFrame frame;
	private Board board;
	public GUI() throws IOException {
		frame = new JFrame("Chess");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize(906,929);
		frame.setResizable(false);
		frame.setLocation((int)(screenSize.width-906)/2,(int)(screenSize.height-929)/2);
		frame.setVisible(true);
		board = new Board();
		frame.add(board.getPanel());
		frame.validate();
	}
	public static void main(String[] args) throws IOException {
		GUI gui = new GUI();
	}
}
