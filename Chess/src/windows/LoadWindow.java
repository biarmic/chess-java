package windows;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import managers.GameManager;

public class LoadWindow extends Window {
	private SaveSlot[] slots = new SaveSlot[3];
	public LoadWindow() {
		super(750,360);
		JLabel label = new JLabel("<html><div align='center'>Load Slots</div></html>",JLabel.CENTER);
		label.setBounds(20,20,710,40);
		label.setFont(new Font(GameManager.font.getName(),Font.PLAIN,30));
		label.setForeground(Color.white);
		add(label,new Integer(0));
		for(int i = 0; i < 3; i++) {
			slots[i] = new SaveSlot(i,25+250*i,65);
			add(slots[i],new Integer(0));
			final int a = i;
			slots[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent event) {
					Screen.removeFromGlassPane(LoadWindow.this);
					Thread thread = new Thread() {
						@Override
						public void run() {
							GameManager.load(a);
						}
					};
					thread.start();
				}
			});
		}
		Button close = new Button("Close",300,280,150,60);
		add(close,new Integer(1));
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				Screen.removeFromGlassPane(LoadWindow.this);
			}
		});
	}
	public void updateSlots() {
		for(SaveSlot slot : slots) {
			slot.updateName();
		}
	}
}
