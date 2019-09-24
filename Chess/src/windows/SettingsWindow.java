package windows;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JLabel;

import managers.AudioManager;
import managers.GameManager;

public class SettingsWindow extends Window {
	private Button colorButton;
	public SettingsWindow() {
		super(700,520);
		JLabel label = new JLabel("<html><div align='center'>Settings</div></html>",JLabel.CENTER);
		label.setBounds(20,20,660,40);
		label.setFont(new Font(GameManager.font.getName(),Font.PLAIN,30));
		label.setForeground(Color.white);
		add(label,new Integer(0));
		readSettings();
		Button[] buttons = new Button[4];
		JLabel[] labels = new JLabel[5];
		labels[0] = new JLabel("Sound");
		labels[1] = new JLabel("The Chessmaster fx");
		labels[2] = new JLabel("Load animation");
		labels[3] = new JLabel("<html><div align='left'>Continue previous game after restart</div></html>");
		labels[4] = new JLabel("Theme color");
		for(int i = 0; i < 5; i++) {
			labels[i].setBounds(20,80+60*i+(i==4 ? 60 : 0),510,40+(i==3 ? 60 : 0));
			labels[i].setFont(new Font(GameManager.font.getName(),Font.PLAIN,30));
			labels[i].setForeground(Color.white);
			add(labels[i],new Integer(0));
			if(i!=4) {
				buttons[i] = new Button(getSetting(i) ? 0 : 1,550,75+60*i+(i==3 ? 30 : 0),100,50,"On","Off");
				add(buttons[i],new Integer(1));
			}
		}
		colorButton = new Button(ColorWindow.THEME_COLOR,550,375,100,50);
		add(colorButton);
		buttons[0].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				AudioManager.SOUND = !AudioManager.SOUND;
				changeSetting(0,AudioManager.SOUND);
			}
		});
		buttons[1].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				AudioManager.CHESSMASTER_FX = !AudioManager.CHESSMASTER_FX;
				changeSetting(1,AudioManager.CHESSMASTER_FX);
			}
		});
		buttons[2].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				GameManager.LOAD_ANIMATION = !GameManager.LOAD_ANIMATION;
				changeSetting(2,GameManager.LOAD_ANIMATION);
			}
		});
		buttons[3].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				GameManager.CONTINUE_GAME = !GameManager.CONTINUE_GAME;
				changeSetting(3,GameManager.CONTINUE_GAME);
			}
		});
		colorButton.addMouseListener(new MouseAdapter() {
			private ColorWindow colorWindow = new ColorWindow(SettingsWindow.this);
			@Override
			public void mouseClicked(MouseEvent event) {
				Screen.addToGlassPane(colorWindow);
			}
		});
		Button close = new Button("Close",275,440,150,60);
		add(close,new Integer(1));
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				Screen.removeFromGlassPane(SettingsWindow.this);
			}
		});
	}
	private void readSettings() {
		List<String> lines = Collections.emptyList();
	    try { 
	      lines = Files.readAllLines(Paths.get("Chess/src/others/settings.txt"),StandardCharsets.UTF_8); 
	    } catch (IOException e) { 
	      e.printStackTrace(); 
	    }
	    AudioManager.SOUND = Boolean.parseBoolean(lines.get(0));
	    AudioManager.CHESSMASTER_FX = Boolean.parseBoolean(lines.get(1));
	    GameManager.LOAD_ANIMATION = Boolean.parseBoolean(lines.get(2));
	    GameManager.CONTINUE_GAME = Boolean.parseBoolean(lines.get(3));
	    ColorWindow.COLOR_MODE = ColorMode.values()[Integer.parseInt(lines.get(4))];
	    StringTokenizer tokenizer = new StringTokenizer(lines.get(5),",");
	    if(ColorWindow.COLOR_MODE==ColorMode.negative) {
			ColorWindow.THEME_COLOR = new Color(Color.HSBtoRGB(Float.parseFloat(tokenizer.nextToken()),Float.parseFloat(tokenizer.nextToken()),Float.parseFloat(tokenizer.nextToken())));
			ColorWindow.OPPOSITE_COLOR = new Color(255-ColorWindow.THEME_COLOR.getRed(),255-ColorWindow.THEME_COLOR.getGreen(),255-ColorWindow.THEME_COLOR.getBlue());
		}else if(ColorWindow.COLOR_MODE==ColorMode.mono) {
			Color color = new Color(Color.HSBtoRGB(Float.parseFloat(tokenizer.nextToken()),Float.parseFloat(tokenizer.nextToken()),Float.parseFloat(tokenizer.nextToken())));
			ColorWindow.THEME_COLOR = color.brighter().brighter();
			ColorWindow.OPPOSITE_COLOR = color.darker().darker();
		}else {
			ColorWindow.THEME_COLOR = new Color(Color.HSBtoRGB(Float.parseFloat(tokenizer.nextToken()),Float.parseFloat(tokenizer.nextToken()),Float.parseFloat(tokenizer.nextToken())));
			tokenizer = new StringTokenizer(lines.get(6),",");
			ColorWindow.OPPOSITE_COLOR = new Color(Color.HSBtoRGB(Float.parseFloat(tokenizer.nextToken()),Float.parseFloat(tokenizer.nextToken()),Float.parseFloat(tokenizer.nextToken())));
		}
	}
	private boolean getSetting(int index) {
		try { 
			List<String> lines = Files.readAllLines(Paths.get("Chess/src/others/settings.txt"),StandardCharsets.UTF_8);
			return Boolean.parseBoolean(lines.get(index));
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
		return false;
	}
	private void changeSetting(int index, boolean value) {
		try { 
			String str = "";
			List<String> lines = Files.readAllLines(Paths.get("Chess/src/others/settings.txt"),StandardCharsets.UTF_8);
			for(int i = 0; i < lines.size(); i++) {
				if(i==index) {
					str += value+"\n";
				}else {
					str += lines.get(i)+"\n";
				}
			}
			FileWriter fw = new FileWriter(new File("Chess/src/others/settings.txt"));
			fw.write(str);
			fw.close();
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
	}
	public void updateColor() {
		colorButton.setBackground(ColorWindow.THEME_COLOR);
	}
}
