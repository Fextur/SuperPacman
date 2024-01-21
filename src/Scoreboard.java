import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Scoreboard extends JPanel
{
	enum info {rank, score, initial}
	private String hsDirectory = "Assets/Misc/HighscoresClassic.txt";
	private String sbDirectory = "Assets/Sprites/Screens/";
	private ImageIcon sbImage;
	private BufferedReader highscoreFile;
	private int newHigh = 0;
	private int newHighCharCounter = 0;
	public boolean canReset = true;
	public int keyCode = 0;
	private Point pos = new Point();
	private Font font;
	private boolean reversed = false;
	
	public Scoreboard(int points, Font f, boolean reversed, int diff) throws FileNotFoundException, FontFormatException, IOException
	{
		this.reversed = reversed;
		//this.reversed = false;
		if(this.reversed) hsDirectory = "Assets/Misc/HighscoresReversed"+diff+".txt";
		else hsDirectory = "Assets/Misc/HighscoresClassic.txt";
		font = f;
		sbImage = new ImageIcon(sbDirectory+"Scoreboard.png");
		try
		{
		    highscoreFile = new BufferedReader(new FileReader(hsDirectory));
		    highscoreFile.close();
		}
		catch (FileNotFoundException e)
		{
	    	newHighScoreFile(hsDirectory);
		}
		newHigh = updateHighscores(points);
	}

	public void drawScoreboard(Graphics g) throws IOException 
	{
		if(newHigh != 0 && newHighCharCounter < 3)
		{
			canReset = false;
			if(keyCode != 0) updateInitial();
			sbImage = new ImageIcon("Assets/Sprites/Screens/ScoreboardNewHighScore.png");
		}
		else
		{
			canReset = true;
			sbImage = new ImageIcon("Assets/Sprites/Screens/ScoreboardRestart.png");
		}
		if(sbImage.getIconHeight() < 0)
			throw new FileNotFoundException("ScreenImage not found");
		sbImage.paintIcon(this, g, 0, 0);
		highscoreFile = new BufferedReader(new FileReader(hsDirectory));
		String line;
		info lineInfo = info.rank; 
		pos.y = 235;
		g.setFont(font); 
		int rank = 1;
		while((line = highscoreFile.readLine()) != null)
	    {
			g.setColor(colorSwitch(rank));
			switch(lineInfo)
			{
				case rank:
					pos.x = 95;
					lineInfo = info.score;
					break;
				case score:
					pos.x = 230;
					lineInfo = info.initial;
					break;
				case initial:
					pos.x = 370;
					lineInfo = info.rank;
					rank++;
					break;
			}
			if(lineInfo == info.score) line = rankSwitch(rank);
			g.drawString(line, (int) (pos.x - String.valueOf(line).length()* 10) , pos.y);
			if(lineInfo == info.rank) pos.y += 40;
	    }
	    highscoreFile.close();
	}
	
	public static void newHighScoreFile(String hsDirectory) throws IOException
	{
		StringBuffer inputBuffer = new StringBuffer();
		for(int i = 1; i <= 10; i++)
		{
			inputBuffer.append(rankSwitch(i));
			inputBuffer.append('\n');
			inputBuffer.append("0");
			inputBuffer.append('\n');
			inputBuffer.append("   ");
			inputBuffer.append('\n');
		}
		FileOutputStream fileOut = new FileOutputStream(hsDirectory);
        fileOut.write(inputBuffer.toString().getBytes());
        fileOut.close();
	}

	private int updateHighscores(int points) throws NumberFormatException, IOException
	{
		info lineInfo = info.rank; 
		int rank = 1;
		int newHigh = 0;
		String line;
		highscoreFile = new BufferedReader(new FileReader(hsDirectory));
		StringBuffer inputBuffer = new StringBuffer();
		String pastScore = "", pastInitial = "";
		try
		{
			while((line = highscoreFile.readLine()) != null)
		    {
				switch(lineInfo)
				{
					case rank:
						inputBuffer.append(rankSwitch(rank));
						lineInfo = info.score;
						break;
					case score:
						lineInfo = info.initial;
						if(newHigh != 0) inputBuffer.append(pastScore);
						else if(points > Integer.parseInt(line) && !reversed)
						{
							inputBuffer.append(points + "");
							newHigh = rank;
						}
						else if((points < Integer.parseInt(line) || Integer.parseInt(line) == 0) && reversed)
						{
							inputBuffer.append(points + "");
							newHigh = rank;
						}
						else inputBuffer.append(line);
						pastScore = line;
						break;
					case initial:
						if(newHigh == rank) inputBuffer.append("   ");
						else if(newHigh != 0) inputBuffer.append(pastInitial);
						else inputBuffer.append(line);
						lineInfo = info.rank;
						rank++;
						pastInitial = line;
						break;
				}
				inputBuffer.append('\n');
		    }
		}
		catch(NumberFormatException e)
		{
			newHighScoreFile(hsDirectory);
			String message = "Highscore Data was corrupted, resetted";
			JOptionPane.showMessageDialog(new JFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);

		}
	    highscoreFile.close();
		if(newHigh != 0)
		{
			FileOutputStream fileOut = new FileOutputStream(hsDirectory);
	        fileOut.write(inputBuffer.toString().getBytes());
	        fileOut.close();
		}
		return newHigh;
	}

	private Color colorSwitch(int i)
	{
		switch(i)
		{
			case 1:
				return Color.LIGHT_GRAY;
			case 2:
				return Color.RED;
			case 3:
				return Color.ORANGE;
			case 4:
				return Color.PINK;
			case 5:
				return new Color(255,204,51);
			case 6:
				return Color.YELLOW;
			case 7:
				return new Color(0,153,0);
			case 8:
				return Color.YELLOW;
			case 9:
				return new Color(51,204,255);
			case 10:
				return new Color(0,255,102);
		}
		return Color.BLACK;
	}
	
	private void updateInitial() throws IOException
	{
		info lineInfo = info.rank; 
		int rank = 1;
		String line;
		highscoreFile = new BufferedReader(new FileReader(hsDirectory));
		StringBuffer inputBuffer = new StringBuffer();
		while((line = highscoreFile.readLine()) != null)
	    {
			switch(lineInfo)
			{
				case rank:
					inputBuffer.append(rankSwitch(rank));
					lineInfo = info.score;
					break;
				case score:
					inputBuffer.append(line);
					lineInfo = info.initial;
					break;
				case initial:
					if(newHigh == rank && keyCode != 0)
					{
						newHighCharCounter++;
						if(line.equals("   ")) inputBuffer.append((char)keyCode);
						else inputBuffer.append(line + (char)keyCode);
						keyCode = 0;
					}
					else inputBuffer.append(line);
					lineInfo = info.rank;
					rank++;
					break;
			}
			inputBuffer.append('\n');
	    }
	    highscoreFile.close();
		FileOutputStream fileOut = new FileOutputStream(hsDirectory);
        fileOut.write(inputBuffer.toString().getBytes());
        fileOut.close();
	}
	
	private static String rankSwitch(int i)
	{
		switch(i)
		{
			case 1:
				return "1st";
			case 2:
				return "2nd";
			case 3:
				return "3rd";
			default:
				return i + "th";
			
		}
	}
	
}
