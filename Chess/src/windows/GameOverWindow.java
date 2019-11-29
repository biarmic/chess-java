package windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import managers.GameManager;

public class GameOverWindow extends Window {
	public GameOverWindow(String text) {
		super(400, 180);
		JLabel label = new JLabel("<html><div align='center'>" + text + "</div></html>", JLabel.CENTER);
		label.setBounds(20, 25, 360, 60);
		label.setFont(new Font(GameManager.font.getName(), Font.PLAIN, 50));
		label.setForeground(Color.white);
		add(label, new Integer(0));
		Button close = new Button("Okay", 100, 100, 200, 60);
		add(close, new Integer(1));
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				Screen.removeFromGlassPane(GameOverWindow.this);
			}
		});
	}
}
