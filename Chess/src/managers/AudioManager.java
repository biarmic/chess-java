package managers;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioManager {
	private static ArrayList<Clip> clips = new ArrayList<Clip>();
	private static HashMap<String,File> soundList = new HashMap<>();
	private static HashMap<String,File> chessmasterList = new HashMap<>();
	public static boolean SOUND;
	public static boolean CHESSMASTER_FX;
	public AudioManager() {
		soundList.put("move",(new File("src/sounds/move.wav")));
		chessmasterList.put("white-move",(new File("src/sounds/chessmaster/white-move.wav")));
		chessmasterList.put("black-move",(new File("src/sounds/chessmaster/black-move.wav")));
		chessmasterList.put("eat",(new File("src/sounds/chessmaster/eat.wav")));
		chessmasterList.put("check",(new File("src/sounds/chessmaster/check.wav")));
		chessmasterList.put("checkmate",(new File("src/sounds/chessmaster/checkmate.wav")));
	}
	public static void playSound(String key) {
		if(SOUND) {
			try {
				stopClips();
				Clip clip = AudioSystem.getClip();
				clips.add(clip);
				if(CHESSMASTER_FX) {
					if(key.equals("move"))
						clip.open(AudioSystem.getAudioInputStream(chessmasterList.get(GameManager.turnColor+"-move")));
					else
						clip.open(AudioSystem.getAudioInputStream(chessmasterList.get(key)));
				}else
					clip.open(AudioSystem.getAudioInputStream(soundList.get("move")));
				clip.start();
			}catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
				e.printStackTrace();
			}
		}
	}
	private static void stopClips() {
		while(clips.size()>0)
			clips.remove(0).stop();
	}
}
