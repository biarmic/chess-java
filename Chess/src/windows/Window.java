package windows;

import javax.swing.JLayeredPane;
import javax.swing.border.LineBorder;
import java.awt.Color;

public abstract class Window extends JLayeredPane {
	public static final LineBorder BORDER = new LineBorder(Color.white, 4);

	public Window(int width, int height) {
		this();
		setBounds((963 - width) / 2, (929 - height) / 2, width, height);
		setPreferredSize(getSize());
	}

	public Window(int x, int y, int width, int height) {
		this();
		setBounds(x, y, width, height);
		setPreferredSize(getSize());
	}

	private Window() {
		setBackground(Color.black);
		setBorder(BORDER);
		setOpaque(true);
	}
}
