import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class MovingObject extends JPanel
{
	enum direction {left, right, up, down, idle};
	protected ImageIcon sprite;
	public Point pos;
	protected int wid = 24;
	protected int height = 24;
	protected direction dir = direction.idle;
	protected int moveOffset = 16;
	private Color bad = new Color(28,28,215);
	protected int spirteRecycler = 0;
	protected String Directory;
	protected long updateTime;
	protected int speed;
	public boolean Lose = false;
	private BufferedImage map = null;
	protected int level = 1;
	protected int keyCode;
	protected boolean ai;
	public int diff;
	protected int[] releventKeys;
	
	public MovingObject(int startX, int startY, String spriteDirectory, int fullSpeedPrecent, int level, int[] releventKeys) throws IOException
	{
		pos = new Point();
		pos.x = startX;
		pos.y = startY;
		Directory = spriteDirectory;
		this.releventKeys = releventKeys;
		speed = fullSpeedPrecent;
		levelDelayBonus();
		updateTime = 10000/speed + System.currentTimeMillis();
		map = ImageIO.read(new File("Assets/Sprites/Screens/Map.png"));
		if(map.getHeight() < 0)
			throw new FileNotFoundException("Map not found");
		keyCode = 0;
	}
	
	public abstract void Lost(Graphics g) throws FileNotFoundException;
	
	public abstract void updatePos() throws AWTException;
	
	public void draw(Graphics g) throws AWTException, FileNotFoundException
	{
		if(Lose)
		{
			Lost(g);
			return;
		}

		if(updateTime < System.currentTimeMillis())
		{
			spirteRecycler++;
			if(spirteRecycler > 3) spirteRecycler = 0;
			
			updatePos();
			if(!inGhostBlock() && !(dir == direction.idle && pos.x == 216 && pos.y == 363))
			{
				stabalizePos();
			}
			checkTeleport();
			updateTime = 10000/speed + System.currentTimeMillis();
		}
		String animName;
		if(dir == null) animName = "Idle";
		else switch(dir)
		{
			case up:
				animName = "Up";
				break;
			case down:
				animName = "Down";
				break;
			case right:
				animName = "Right";
				break;
			case left:
				animName = "Left";
				break;
			case idle:
			default:
				animName = "Idle";
				break;
		}
		sprite = new ImageIcon("Assets/Sprites/"+Directory+"/"+animName+spirteRecycler+".png");
		if(sprite.getIconHeight() < 0)
			throw new FileNotFoundException(animName + spirteRecycler + " not found");
		sprite.paintIcon(this, g, pos.x, pos.y + 115);
	}
	
	private void stabalizePos()
	{
		int lo = (pos.x - 15)%16;
		if(lo > 8) lo = 1;
		else lo = 0;
		pos.x = (pos.x - 15)/16;
		pos.x = (pos.x+lo)*16 + 15;
		lo = (pos.y - 12)%16;
		if(lo > 8) lo = 1;
		else lo = 0;
		pos.y = (pos.y - 12)/16;
		pos.y = (pos.y+ lo)*16 + 12;
	}
	
	public boolean checkAvailability(direction checkDir) throws AWTException
	{
		return checkAvailability(checkDir, pos.x, pos.y);
	}
	
	public boolean checkAvailability(direction checkDir, int x, int y) throws AWTException
	{
		if(map == null) return false;
		Color c = null;
		Rectangle mapRec = new Rectangle(0, 0, map.getWidth(), map.getHeight());
		switch (checkDir)
		{
			case up:
				if(x >= 210 && x <= 222 && y == 183) return true;
				for(int i = 0; i < wid; i++)
				{
					for(int j = 0; j < height; j++)
					{
						if(!mapRec.contains(new Point(i+ x, j + y - moveOffset))) continue;
						c = new Color(map.getRGB(i + x, j + y - moveOffset));
						if(c.equals(bad)) 
							return false;
					}
				}
				break;
			case down:
				if(y == 172 && (x == 207 || x == 223))
				{
					return Directory == "Eyes";
				}
				for(int i = 0; i < wid; i++)
				{
					for(int j = 0; j < height; j++)
					{

						if(!mapRec.contains(new Point(i+ x, j + y + moveOffset))) continue;
						c = new Color(map.getRGB(i + x, j + y + moveOffset));
						if(c.equals(bad)) 
							return false;
					}
				}
				break;
			case right:
				for(int i = 0; i < wid; i++)
				{
					for(int j = 0; j < height; j++)
					{
						if(i + x  + moveOffset > 447 && y > 215  && y < 225) return true;
						if(!mapRec.contains(new Point(i+x + moveOffset, j + y))) continue;
						c = new Color(map.getRGB(i + x + moveOffset, j + y));
						if(c.equals(bad)) return false;	
					}
				}
				break;
			case left:
				for(int i = 0; i < wid; i++)
				{
					for(int j = 0; j < height; j++)
					{
						if(i + x - moveOffset < 16 && y > 215 && y < 225) return true;
						if(!mapRec.contains(new Point(i+ x - moveOffset, j + y))) continue;
						c = new Color(map.getRGB(i + x - moveOffset, j + y));
						if(c.equals(bad)) return false;	
					}
				}
				break;
			default:
				return true;
		}
		return true;
	}

	private void checkTeleport()
	{
		if(pos.x > 431 && pos.y > 215  && pos.y < 225  && dir == direction.right) pos.x = 16;
		if(pos.x < 16 && pos.y > 215 && pos.y < 225  && dir == direction.left) pos.x = 431;
	}
	
	protected boolean inGhostBlock()
	{
		if(pos.x > 172  && pos.x < 285 && ((pos.y > 175 && pos.y < 265)))
			return true;
		return false;
			
	}
	
	protected void levelDelayBonus()
	{
		if(level >= 2 && level <= 4) speed += 10;
		if(level >= 5) speed += 10;
	}
	
	protected abstract direction FindDirectionFromInput() throws AWTException;

	public void Input(KeyEvent e) throws AWTException
	{
		if (e.getKeyCode() == keyCode) return;
		for(int i = 0; i < releventKeys.length; i++)
		{
			if(releventKeys[i] == e.getKeyCode())
			{
				keyCode = e.getKeyCode();
				break;
			}
		}
		return;
	}

	public void InputRelease(KeyEvent e) throws AWTException
	{
		if (e.getKeyCode() == keyCode) keyCode = 0;
	}
	
	protected direction targetDirection(Point target, Point pos, direction dir, boolean up, boolean down, boolean left, boolean right, boolean canTurn) throws AWTException
	{
		Point upPos = new Point(pos.x, pos.y - moveOffset);
		Point downPos = new Point(pos.x, pos.y + moveOffset);
		Point rightPos = new Point(pos.x + moveOffset, pos.y);
		Point leftPos = new Point(pos.x - moveOffset, pos.y);
		direction nextDir = direction.idle;
		double minDist = 100000;
		if((dir != direction.down || canTurn )&& up && checkAvailability(direction.up, pos.x, pos.y) && target.distance(upPos) < minDist) 
		{
			minDist = target.distance(upPos);
			nextDir = direction.up;
		}
		if((dir != direction.up || canTurn) && down && checkAvailability(direction.down, pos.x, pos.y) && target.distance(downPos) < minDist)
		{
			minDist = target.distance(downPos);
			nextDir = direction.down;
		}
		if((dir != direction.left || canTurn) && right && checkAvailability(direction.right, pos.x, pos.y) && target.distance(rightPos) < minDist)
		{
			minDist = target.distance(rightPos);
			nextDir = direction.right;
		}
		if((dir != direction.right || canTurn )&& left && checkAvailability(direction.left, pos.x, pos.y) && target.distance(leftPos) < minDist)
		{
			minDist = target.distance(leftPos);
			nextDir = direction.left;
		}
		return nextDir;
	}

	protected direction targetDirection(Point target) throws AWTException
	{
		return targetDirection(target, pos, dir, true, true, true, true, false);
	}	

}
