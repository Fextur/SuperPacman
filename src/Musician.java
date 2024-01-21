import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Musician 
{
	public String LastPlayedName = "";
	public long LastPlayedTime = -1000;

	public Musician()
	{
		
	}
	
	public void playSound(String sfx) throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		LastPlayedName = sfx;
		LastPlayedTime = System.currentTimeMillis();
	    AudioInputStream stream;
	    AudioFormat format;
	    DataLine.Info info;
	    Clip clip;
	    stream = AudioSystem.getAudioInputStream(new File("Assets/SFX/" + sfx + ".wav"));
	    format = stream.getFormat();
	    info = new DataLine.Info(Clip.class, format);
	    clip = (Clip) AudioSystem.getLine(info);
	    clip.open(stream);
	    clip.start();
	}
	
}
