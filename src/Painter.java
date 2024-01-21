import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Painter extends JPanel implements ActionListener, KeyListener
{
	enum ScreenState {title,chooseMode, chooseDiff, chooseGhost,  game, level, score};
	private Screen screen;
	private Pac pac;
	private RedGhost blinky;
	private PinkGhost pinky;
	private AquaGhost inky;
	private OrangeGhost clyde;
	private Fruit item;
	private Life life;
	private Score score;
	private Highscore hs;
	private Scoreboard scoreboard;
	private Musician mozart;
	private Dot dot;
	private GameData data;
	private BigDot bigDot;
	private Point[] dots = new Point[300];
	private int dotCounter = 0;
	private Point[] bigDots = new Point[4];
	private int bigDotCounter = 4;
	private Timer timer;
	private int points = 0;
	private int frightCounter = 0;
	private boolean[] ghostState = {true, true, true, true};
	private int eatCombo = 0;
	private int level = 1;
	private boolean extraLife = false;
	private ScreenState state = ScreenState.title;
	private Font font;
	private boolean deathTime = false;
	private int lifes = 3;
	private ChooseGhost cgScreen;
	private boolean reversed = false;
	
	public Painter() throws IOException, AWTException, FontFormatException
	{
		level = 1;
		pac = new Pac(level);
		screen = new Screen();
		life = new Life();
		blinky = new RedGhost(level);
		pinky = new PinkGhost(level);
		inky = new AquaGhost(level);
		clyde = new OrangeGhost(level);
		score = new Score();
		font = score.font;
		scoreboard = new Scoreboard(0,font, false, 1);
		setFocusable(true);
		requestFocus();
		dotsInitalState();
		bigDotsInitalState();
		item = new Fruit(level);
		mozart = new Musician();
		dot = new Dot();
		bigDot = new BigDot();
		cgScreen = new ChooseGhost();
		data = new GameData();
		timer = new Timer(0, this);
		timer.start();
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paintComponent(g);
		try 
		{
			switch(state)
			{
				case title:
				{
					screen.drawScreen(g, "TitleScreen");
					break;
				}
				case chooseMode:
				{
					screen.drawScreen(g, "ChooseMode");
					break;
				}
				case chooseGhost:
				{
					cgScreen.drawChooseScreen(g);
					break;
				}
				case chooseDiff:
				{
					screen.drawScreen(g, "ChooseDifficulty");
					break;
				}
				case level:
				{
					screen.drawScreen(g, "LevelScreen");
					g.setColor(Color.RED);
					g.setFont(font); 
					String s = "LEVEL " + level;
					g.drawString(s, (int) (228 - String.valueOf(s).length()* 10) , 300);
					break;
				}
				case score:
				{
					afterGame(g);
					break;
				}
				case game:
				{
					inGame(g);
					break;
				}
			}
		}
		catch (Exception e)
		{
			errorDialog(e);
		} 
		g.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		timer.restart();
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_9)
		{
			state = ScreenState.level;
			return;
		}
		try 
		{
			blinky.Input(e);
			pinky.Input(e);
			inky.Input(e);
			clyde.Input(e);
			pac.Input(e);
		} 
		catch (AWTException e1) 
		{
			errorDialog(e1);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		try
		{
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE && (state != ScreenState.score || scoreboard.canReset))
				System.exit(0);
			switch(state)
			{
				case title:
				{
					if(e.getKeyCode() == KeyEvent.VK_ENTER)
					{
						state = ScreenState.chooseMode;
					}
					break;
				}
				case chooseMode:
				{
					if(e.getKeyCode() == KeyEvent.VK_ENTER)
					{
						blinky.ai = true;
						pinky.ai = true;
						inky.ai = true;
						clyde.ai = true;
						pac.ai = false;
						reversed = false;
						state = ScreenState.level;
						mozart.playSound("LevelIntermission");
						hs = new Highscore(false, 0);
					}
					if(e.getKeyCode() == KeyEvent.VK_SPACE)
					{
						reversed = true;
						state = ScreenState.chooseDiff;
					}
					break;
				}
				case chooseDiff:
				{
					if(e.getKeyCode() == KeyEvent.VK_1)
					{
						pac.diff = 1;
						pac.limitDepth = 5;
						state = ScreenState.chooseGhost;
					}
					if(e.getKeyCode() == KeyEvent.VK_2)
					{
						pac.diff = 2;
						pac.limitDepth = 7;
						state = ScreenState.chooseGhost;
					}
					if(e.getKeyCode() == KeyEvent.VK_3)
					{
						pac.diff = 3;
						pac.limitDepth = 15;
						pac.ghostScore  = 90;
						state = ScreenState.chooseGhost;
					}
					blinky.diff = pac.diff;
					pinky.diff = pac.diff;
					inky.diff = pac.diff;
					clyde.diff = pac.diff;
					hs = new Highscore(true, pac.diff);
					break;
				}
				case chooseGhost:
				{
					if(e.getKeyCode() == KeyEvent.VK_ENTER)
					{
						blinky.ai = !cgScreen.redState;
						pinky.ai = !cgScreen.pinkState;
						inky.ai = !cgScreen.aquaState;
						clyde.ai = !cgScreen.orangeState;
						pac.ai = true;
						state = ScreenState.level;
						mozart.playSound("LevelIntermission");
					}
					else cgScreen.keyCode = e.getKeyCode();

					blinky.keyCode = 0;
					pinky.keyCode = 0;
					inky.keyCode = 0;
					clyde.keyCode = 0;
				}
				case level:
				{
					if(e.getKeyCode() == KeyEvent.VK_ENTER)
					{
						state = ScreenState.game;
						blinky.startTime = System.currentTimeMillis();
						pinky.startTime = System.currentTimeMillis();
						inky.startTime = System.currentTimeMillis();
						clyde.startTime = System.currentTimeMillis();
					}
					break;
				}
				case score:
				{
					if(isPrintableChar(e.getKeyChar())) scoreboard.keyCode = e.getKeyCode();
					else if (e.getKeyCode() == KeyEvent.VK_ENTER && scoreboard.canReset) resetLevel(false);
					break;
				}
				case game:
				{
					blinky.InputRelease(e);
					pinky.InputRelease(e);
					inky.InputRelease(e);
					clyde.InputRelease(e);
					pac.InputRelease(e);
					break;
				}
			}
		}
		catch (Exception e1)
		{
			errorDialog(e1);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) 
	{
	}
	
	private void dotsInitalState() throws FileNotFoundException
	{
		dotCounter = 0;
		for(int i = 0; i < 29; i++)
		{
			for(int j = 0; j < 26; j++)
			{
				boolean exist = false;
				switch(i)
				{
					case 0:
					case 19:
						if(j != 12 && j != 13) exist = true;
						break;
					case 1:
					case 2:
					case 3:
					case 20:
					case 21:
						if(j == 0 || j == 5 || j == 11 || j == 14 || j == 20 || j == 25) exist = true;
						break;
					case 4:
					case 28:
						 exist = true;
						break;
					case 5:
					case 6:
						if(j == 0 || j == 5 || j == 8 || j == 17 || j == 20 || j == 25) exist = true;
						break;
					case 7:
					case 25:
						if(j == 6 || j == 7 || j == 12 || j == 13 || j == 18 || j == 19);
						else exist = true;
						break;
					case 22:
						if(j == 3 || j == 4 || j == 12 || j == 13 || j == 21 || j == 22);
						else exist = true;
						break;
					case 23:
					case 24:
						if(j == 2|| j == 5 || j == 8 || j == 17 || j == 20 || j == 23) exist = true;
						break;
					case 26:
					case 27:
						if(j == 0 || j == 11 || j == 14 || j == 25) exist = true;
						break;
					default:
						if(j == 5 || j == 20) exist = true;
						break;
				}
				if(exist) dots[dotCounter++] = new Point(20 + j* 16, i*16 + 16);
			}
		}
	}
	
	private void bigDotsInitalState() throws FileNotFoundException
	{
		bigDots[0] = new Point(20, 2*16 + 16);
		bigDots[1] = new Point(20 + 25 * 16, 2*16 + 16);
		bigDots[2] = new Point(20, 22*16 + 16);
		bigDots[3] = new Point(20 + 25 * 16, 22*16 + 16);
	}

	private void inGame(Graphics g) throws FileNotFoundException, FontFormatException, IOException, UnsupportedAudioFileException, LineUnavailableException
	{
		screen.drawScreen(g, "MapWithBorders");
		score.points = points;
		hs.points = points;
		score.draw(g);
		hs.draw(g);
		updateData();
		for(int i = 0; i < dotCounter; i++)
		{
			if(!dot.drawDot(g,dots[i], data.pac))
			{
				points += 10;
				dots[i] = dots[--dotCounter];
				i--;
				if(!mozart.LastPlayedName.equals("Chomp") || System.currentTimeMillis() -  mozart.LastPlayedTime > 100)
					mozart.playSound("Chomp");
			}
		}
		item.drawFruit(g, dotCounter);
		item.pac = data.pac;
		int fruitPoints = item.eatCheck();
		if(fruitPoints != 0)
		{
			points += fruitPoints;
			mozart.playSound("EatFruit");
		}
		if(dotCounter == 0)
		{
			resetLevel(true);
			return;
		}
		for(int i = 0; i < bigDotCounter; i++)
		{
			if(!bigDot.drawDot(g, bigDots[i], data.pac))
			{
				blinky.setFreightMode();
				pinky.setFreightMode();
				inky.setFreightMode();
				clyde.setFreightMode();
				bigDots[i] = bigDots[--bigDotCounter];
				i--;
				points += 50;
				frightCounter = 25;
				mozart.playSound("FreightSiren");
			}
		}
		if(frightCounter > 0)
		{
			for(int i = 0; i < 4; i++)
			{
				if(ghostState[i] && data.frightCounter[i] == -1)
				{
					ghostState[i] = false;
					points += Math.pow(2, ++eatCombo) * 100;
					mozart.playSound("EatGhost");
				}
				else if(data.frightCounter[i] != 0) frightCounter = data.frightCounter[i];
				if(eatCombo > 3) eatCombo = 3;
			}
			
		}
		else eatCombo = 0;
		for(int i = 0; i < 4; i++) ghostState[i] = data.frightCounter[i] != -1;
		if(extraLife == false && points >= 10000)
		{
			extraLife = true;
			lifes++;
		}
		if(pac.restart)
		{
			if(lifes == 0)
			{
				scoreboard = new Scoreboard(points, font, reversed, pac.diff);
				state = ScreenState.score;
				return;
			}
			pac.restart = false;
			pac.Lose = false;
			deathTime = false;
			try 
			{
				blinky = new RedGhost(level);
				blinky.diff = pac.diff;
				pinky = new PinkGhost(level);
				pinky.diff = pac.diff;
				inky = new AquaGhost(level);
				inky.diff = pac.diff;
				clyde = new OrangeGhost(level);
				clyde.diff = pac.diff;
				blinky.ai = !cgScreen.redState;
				pinky.ai = !cgScreen.pinkState;
				inky.ai = !cgScreen.aquaState;
				clyde.ai = !cgScreen.orangeState;
				pac.ai = reversed;
			} 
			catch (IOException e) 
			{
				errorDialog(e);
			}
		}
		life.drawLife(g, lifes);
		pinky.pacDir = data.pacDir;
		blinky.pacDir = data.pacDir;
		inky.pacDir = data.pacDir;
		clyde.pacDir = data.pacDir;
		pinky.setPacPos(data.pac);
		blinky.setPacPos(data.pac);
		inky.setPacPos(data.pac);
		inky.setRedPos(data.ghostPos[0]);
		clyde.setPacPos(data.pac);
		if(blinky.checkCollision()) blinky.collided = true;
		if(pinky.checkCollision()) pinky.collided = true;
		if(inky.checkCollision()) inky.collided = true;
		if(clyde.checkCollision()) clyde.collided = true;
		if((pinky.Lose || blinky.Lose || inky.Lose || clyde.Lose) && !deathTime)
		{
			blinky.Lose = true;
			pinky.Lose = true;
			inky.Lose = true;
			clyde.Lose = true;
			pac.Lose = true;
			pac.spirteRecycler = 0;
			lifes--;
			deathTime = true;
			mozart.playSound("Death");
		}
		if(reversed) pac.data = GameData.copyGameData(data);
		try
		{
			pinky.draw(g);
			blinky.draw(g);
			inky.draw(g);
			clyde.draw(g);
			pac.draw(g);
		}
		catch(Exception e)
		{
			errorDialog(e);
		}			
	}

	private void afterGame(Graphics g) throws IOException
	{
		scoreboard.drawScoreboard(g);
		score.draw(g);
	}

	private boolean isPrintableChar(char c) 
	{
	    Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
	    return (!Character.isISOControl(c)) &&
	            c != KeyEvent.CHAR_UNDEFINED &&
	            block != null &&
	            block != Character.UnicodeBlock.SPECIALS;
	}
	
	private void resetLevel(boolean next) throws IOException, UnsupportedAudioFileException, LineUnavailableException
	{
		if (next)level++;
		else 
		{
			points = 0;
			level = 1;
			lifes = 3;
		}
		int diff = pac.diff;
		int depth = pac.limitDepth;
		pac = new Pac(level);
		pac.diff = diff;
		pac.limitDepth = depth;
		life = new Life();
		blinky = new RedGhost(level);
		blinky.diff = pac.diff;
		pinky = new PinkGhost(level);
		pinky.diff = pac.diff;
		inky = new AquaGhost(level);
		inky.diff = pac.diff;
		clyde = new OrangeGhost(level);
		clyde.diff = pac.diff;
		dotsInitalState();
		bigDotsInitalState();
		bigDotCounter = 4;
		item = new Fruit(level);
		deathTime = false;
		blinky.ai = !cgScreen.redState;
		pinky.ai = !cgScreen.pinkState;
		inky.ai = !cgScreen.aquaState;
		clyde.ai = !cgScreen.orangeState;
		pac.ai = reversed;
		if(reversed && next)
		{
			blinky.startTime = System.currentTimeMillis();
			pinky.startTime = System.currentTimeMillis();
			inky.startTime = System.currentTimeMillis();
			clyde.startTime = System.currentTimeMillis();
			state = ScreenState.game;
		}
		else state = ScreenState.level;
		mozart.playSound("LevelIntermission");
	}
	
	static boolean error = false;
	public static void errorDialog(Exception e)
	{
		if(error) return;
		error = true;
		String message = e.getLocalizedMessage();
		JOptionPane.showMessageDialog(new JFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}
	
	private void updateData()
	{
		Point[] ghostPos = {blinky.pos, pinky.pos, inky.pos, clyde.pos};
		MovingObject.direction[] ghostDir = {blinky.dir, pinky.dir, inky.dir, clyde.dir};
		int[] frightCounter = {blinky.frightCounter, pinky.frightCounter, inky.frightCounter, clyde.frightCounter};
		data.updateData(dots, dotCounter, bigDots, bigDotCounter, ghostPos, ghostDir, frightCounter, item.state, pac.pos, pac.dir);
	}

}
