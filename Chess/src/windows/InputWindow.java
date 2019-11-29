package windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JLabel;
import managers.GameManager;

public class InputWindow extends Window {
	private JLabel fileName = new JLabel("");

	public InputWindow(SaveSlot saveSlot) {
		super(400, 140);
		JLabel label = new JLabel("Enter file name", JLabel.CENTER);
		label.setFont(new Font(GameManager.font.getName(), Font.PLAIN, 30));
		label.setForeground(Color.white);
		label.setBounds(20, 20, 360, 40);
		add(label, new Integer(0));
		fileName.setFont(new Font(GameManager.font.getName(), Font.PLAIN, 30));
		fileName.setForeground(Color.white);
		fileName.setBounds(20, 80, 360, 40);
		add(fileName, new Integer(1));
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE && fileName.getText().length() != 0) {
					fileName.setText(fileName.getText().substring(0, fileName.getText().length() - 1));
				} else if (event.getKeyCode() == KeyEvent.VK_ENTER && fileName.getText().length() > 0) {
					saveSlot.saveFile(fileName.getText());
					Screen.removeFromGlassPane(InputWindow.this);
				} else {
					char key = event.getKeyChar();
					if ((int) key >= 33 && (int) key <= 126 && fileName.getText().length() < 8) {
						fileName.setText(fileName.getText() + key);
					}
				}
			}
		});
	}
}
