import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@SuppressWarnings("serial")
public class Highscore extends TextObject
{
	private int highest = 0;
	int points = 0;
	boolean reversed;
	public Highscore(boolean reversed, int diff) throws FileNotFoundException, FontFormatException, IOException 
	{
		super(315, 90);
		String directory;
		this.reversed = reversed;
		if(reversed) directory = "Assets/Misc/HighscoresReversed"+diff+".txt";
		else directory = "Assets/Misc/HighscoresClassic.txt";
		BufferedReader br;
		try
		{
			br = new BufferedReader(new FileReader(directory));
		}
		catch (FileNotFoundException e)
		{
	    	Scoreboard.newHighScoreFile(directory);
	    	br = new BufferedReader(new FileReader(directory));
		}
	    String line;
	    while((line = br.readLine()) != null)
	    {
	    	if(line.equals("1st") && (line = br.readLine()) != null)
	    	{
	    		try
	    		{
	    			highest = Integer.parseInt(line);
	    		}
	    		catch(NumberFormatException e)
	    		{
	    			
	    		}
	    	}
	    }
	    br.close();
	}

	@Override
	protected String definePrint() 
	{
		if(points > highest && !reversed) highest = points;
		return highest + "";
		
	}
}

