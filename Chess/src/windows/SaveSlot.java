package windows;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.JLabel;
import managers.GameManager;

public class SaveSlot extends Window {
	private int index;
	private JLabel name;
	public SaveSlot(int index, int x, int y) {
		super(x,y,200,200);
		this.index = index;
		name = new JLabel("<html><div align='center'>"+getFileName()+"</div></html>",JLabel.CENTER);
		name.setBounds(8,8,184,184);
		name.setFont(new Font(GameManager.font.getName(),Font.PLAIN,30));
		name.setForeground(Color.white);
		add(name,new Integer(0));
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	public void updateName() {
		name.setText("<html><div align='center'>"+getFileName()+"</div></html>");
	}
	private String getFileName() {
		List<String> lines = Collections.emptyList(); 
	    try { 
	      lines = Files.readAllLines(Paths.get("Chess/src/records/save-slot-"+index+".txt"),StandardCharsets.UTF_8); 
	    } catch (IOException e) { 
	      e.printStackTrace(); 
	    }
	    if(lines.size()==0) {
	    	return "Empty";
	    }
	    return lines.get(0); 
	}
	public void saveFile(String fileName) {
		name.setText("<html><div align='center'>"+fileName+"</div></html>");
		try {
			String str = fileName+"\n";
			BufferedReader br = new BufferedReader(new FileReader(new File("Chess/src/records/main.txt")));
			Iterator<String> iterator = br.lines().iterator();
			while(iterator.hasNext()) {
				str += iterator.next()+"\n";
			}
			br.close();
			FileWriter fw = new FileWriter(new File("Chess/src/records/save-slot-"+index+".txt"));
			fw.write(str);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
