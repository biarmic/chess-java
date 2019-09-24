package windows;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import gamestuff.Dot;
import managers.AudioManager;
import managers.GameManager;

public class Screen extends JFrame {
	private static Screen screen;
	private static AudioManager audioManager = new AudioManager();
	private static GameManager gameManager;
	private static Board board;
	private static GridBagConstraints gbc = new GridBagConstraints();
	public Screen() {
		super("Chess");
		screen = this;
		gameManager = new GameManager(this);
		board = GameManager.board;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(963,929);
		setResizable(false);
		setLocation((int)(screenSize.width-963)/2,(int)(screenSize.height-929)/2);
		try {
			Dot.dot = new ImageIcon(ImageIO.read(getClass().getResource("/images/dot.png")));
			Dot.corners = new ImageIcon(ImageIO.read(getClass().getResource("/images/corners.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		getGlassPane().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if(event.getSource() instanceof JLabel && ((JLabel) event.getSource()).getParent()!=getGlassPane())
					event.consume();
			}
		});
		((JPanel) getGlassPane()).setLayout(new GridBagLayout());
		setVisible(true);
		GameManager.start();
	}
	public static void addToGlassPane(JLayeredPane pane) {
		JPanel glass = ((JPanel) screen.getGlassPane());
		for(Component comp : glass.getComponents()) {
			glass.remove(comp);
			board.add(comp,new Integer(board.highestLayer()+1));
		}
		glass.add(pane,gbc);
		glass.setVisible(true);
		board.repaint();
		glass.repaint();
	}
	public static void removeFromGlassPane(JLayeredPane pane) {
		JPanel glass = ((JPanel) screen.getGlassPane());
		glass.remove(pane);
		for(Component comp : board.getComponentsInLayer(board.highestLayer())) {
			if(comp instanceof Window) {
				board.remove(comp);
				glass.add(comp,gbc);
				break;
			}
		}
		board.repaint();
		glass.repaint();
		glass.setVisible(glass.getComponents().length!=0);
	}
	public static void setClickable(boolean clickable) {
		((JPanel)screen.getGlassPane()).setVisible(!clickable);;
	}
	public static void main(String[] args) {
		new Screen();
	}
}
