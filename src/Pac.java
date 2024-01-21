import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.ImageIcon;

@SuppressWarnings("serial")
public class Pac extends MovingObject
{
	public boolean restart = false;
	public int limitDepth;
	private boolean allDotsEaten;
	public int dotScore = 1;
	public int bigdotScore = -1;
	public int fruitScore = 50;
	public int ghostScore = 70;
	public GameData data;
	
	public class depthCounter
	{
		public int stepCounter;
		public int stepGhostCounter;
		public depthCounter(int stepCounter, int stepGhostCounter)
		{
			this.stepCounter = stepCounter;
			this.stepGhostCounter = stepGhostCounter;
		}
	}
	
	public Pac(int level) throws IOException 
	{
		super(216, 363, "Pac", 80, level, new int[] {KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, 
				KeyEvent.VK_W, KeyEvent.VK_S,KeyEvent.VK_D,KeyEvent.VK_A});
		ai = false;
		if(level >= 5) fruitScore = 70;
		if(level >= 7) fruitScore = 90;
		if(level >= 9) fruitScore = 130;
		if(level >= 11) fruitScore = 200;
	}
	
	public void updatePos() throws AWTException 
	{
		if(!ai)
		{
			direction nextDir = FindDirectionFromInput();
			if(nextDir != direction.idle) dir = nextDir;
			if(!checkAvailability(dir)) return;
		}
		else
		{
			pacAI();
		}
		switch (dir)
		{
			case up:
				pos.y -= moveOffset;
				break;
			case down:
				pos.y += moveOffset;
				break;
			case left:
				pos.x -= moveOffset;
				break;
			case right:
				pos.x += moveOffset;
				break;
			default:
				break;
		
		}
	}
	
	public direction FindDirectionFromInput() throws AWTException
	{
		switch (keyCode)
		{
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				if(checkAvailability(direction.up))
					return direction.up;
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				if(checkAvailability(direction.down))
					return direction.down;
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				if(checkAvailability(direction.left))
					return direction.left;
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				if(checkAvailability(direction.right))
					return direction.right;
				break;	
		}
		return direction.idle;
	}

	@Override
	public void Lost(Graphics g) throws FileNotFoundException
	{
		if(updateTime < System.currentTimeMillis())
		{
			spirteRecycler++;

			updateTime = 10000/speed + System.currentTimeMillis();
		}
		if(spirteRecycler <= 11)
		{
			sprite = new ImageIcon("./Assets/Sprites/PacDeath/Death"+spirteRecycler+".png");
			if(sprite.getIconHeight() < 0)
				throw new FileNotFoundException("Death"+spirteRecycler+ "not found");
			sprite.paintIcon(this, g, pos.x, pos.y + 115);
		}
		else
		{
			spirteRecycler = 0;
			pos.x = 216;
			pos.y = 363;
			dir = direction.idle;
			sprite.paintIcon(this, g, 214, 363);
			restart = true;
		}
	}
	
	private void pacAI() throws AWTException
	{
		int maxPoints = -10000;
		int maxDepth = 0;
		int maxGhostDepth = 0;
		depthCounter counter = new depthCounter(0, 0);
		direction nextDir = direction.idle;
		int points;
		GameData dataCopy = GameData.copyGameData(data);
		boolean up = false;
		boolean down = false;
		boolean right = false;
		boolean left = false;
		if(checkAvailability(direction.up))
		{
			allDotsEaten = false;
			points = dirMinMax(direction.up, pos.x, pos.y - moveOffset, limitDepth, dataCopy, counter);
			dataCopy = GameData.copyGameData(data);
			if(points >= 0) up = true;
		}
		else points = -10000;
		if(points > maxPoints)
		{
			maxPoints = points;
			maxDepth = counter.stepCounter;
			maxGhostDepth = counter.stepGhostCounter;
			nextDir = direction.up;
		}
		if(checkAvailability(direction.down))
		{
			allDotsEaten = false;
			counter.stepCounter = counter.stepGhostCounter = 0;
			points = dirMinMax(direction.down, pos.x, pos.y + moveOffset, limitDepth, dataCopy, counter);
			dataCopy = GameData.copyGameData(data);
			if(points >= 0) down = true;
		}
		else points = -10000;
		
		if(points > maxPoints ||  (points == maxPoints && dataCopy.pacDir == direction.down))
		{
			maxPoints = points;
			maxDepth = counter.stepCounter;
			maxGhostDepth = counter.stepGhostCounter;
			nextDir = direction.down;
		}
		if(checkAvailability(direction.left))
		{
			allDotsEaten = false;
			counter.stepCounter = counter.stepGhostCounter = 0;
			points = dirMinMax(direction.left, pos.x - moveOffset, pos.y, limitDepth, dataCopy, counter);
			dataCopy = GameData.copyGameData(data);
			if(points >= 0) left = true;
		}
		else points = -10000;
		if(points > maxPoints ||
				(points >= 0 && points == maxPoints && maxDepth < counter.stepCounter) ||
				(points < 0 && points == maxPoints && maxGhostDepth > counter.stepGhostCounter))
		{
			maxPoints = points;
			maxDepth = counter.stepCounter;
			maxGhostDepth = counter.stepGhostCounter;
			nextDir = direction.left;
		}
		if(checkAvailability(direction.right))
		{
			allDotsEaten = false;
			counter.stepCounter = counter.stepGhostCounter = 0;
			points = dirMinMax(direction.right, pos.x + moveOffset, pos.y, limitDepth, dataCopy, counter);
			dataCopy = GameData.copyGameData(data);
			if(points >= 0) right = true;
		}
		else points = -10000;
		if(points > maxPoints ||  (points == maxPoints && nextDir == direction.left && dataCopy.pacDir == direction.right) ||
				(points >= 0 && points == maxPoints && maxDepth < counter.stepCounter) ||
				(points < 0 && points == maxPoints && maxGhostDepth > counter.stepGhostCounter))
		{
			maxPoints = points;
			nextDir = direction.right;
		}
		if(maxPoints == 0)
		{
			int badCounter = 0;
			if(!up) badCounter++;
			if(!down) badCounter++;
			if(!right) badCounter++;
			if(!left) badCounter++;
			if(badCounter == 3)
			{
				if(up) nextDir = direction.up;
				if(down) nextDir = direction.down;
				if(right) nextDir = direction.right;
				if(left) nextDir = direction.left;
			}
			else
			{
				double minDist = 10000;
				Point target = null;
				for(int i = 0; i < data.dotCounter; i++)
				{
					Point dot = new Point(data.dots[i].x, data.dots[i].y);
					double dist = pos.distance(dot);
					if(dist < minDist)
					{
						target = dot;
						minDist = pos.distance(target);
					}
				}
				nextDir = targetDirection(target, pos, dir, up, down , left, right, false);
				if (nextDir == direction.idle) 
				{
					nextDir = targetDirection(target);
				}
			}
		}
		dir = nextDir;
	}
	
	private direction targetDirection(Point target, Point pos, direction dir) throws AWTException
	{
		if(diff < 2) return targetDirection(target, pos, dir, true, true, true, true, true);
		else return targetDirection(target, pos, dir, true, true, true, true, false);
	}
	
	private int dirMinMax(direction nextdir, int x, int y, int depth, GameData data, depthCounter counter) throws AWTException
	{
		int bonus = 0;
		if(depth < 0)
		{
			return -10000;
		}
		int savedStepsCounter = 0, savedStepsGhostCounter = 0;
		Point newPos = new Point(x,y);
		for(int i = 0; i < 4; i++)
		{
			if(data.frightCounter[i] >= 0)
			{
				if(Ghost.checkCollision(newPos, data.ghostPos[i], nextdir, data.ghostDir[i]))
				{
					if(data.frightCounter[i] > 1)
					{
						data.frightCounter[i] = -1;
						data.ghostPos[i] = new Point(223, 172);
						data.ghostDir[i] = direction.up;
						if(diff > 1 )
						{
							if (counter.stepCounter == 0) 
								counter.stepCounter = depth;
							bonus += ghostScore;
						}
					}
					else
					{
						if (counter.stepGhostCounter == 0) 
							counter.stepGhostCounter = depth;
						return -1000;
					}
				}
			}
		}
		for(int i = 0; i < 4; i++)
		{
			if(data.frightCounter[i] >= 0)
			{
				direction nDir = direction.idle;
				if(data.frightCounter[i] > 0) data.frightCounter[i]--;
	
				if(data.frightCounter[i] == 0) nDir = targetDirection(newPos, data.ghostPos[i], data.ghostDir[i]);
				else if(data.frightCounter[i] > 0) nDir = targetDirection(AquaGhost.Opposite(newPos, data.ghostPos[i], new Point(0,0)),data.ghostPos[i], data.ghostDir[i]);
				switch(nDir)
				{
					case up:
						data.ghostPos[i].y -= moveOffset;
						data.ghostDir[i] = direction.up;
						break;
					case down:
						data.ghostPos[i].y += moveOffset;
						data.ghostDir[i] = direction.down;
						break;
					case right:
						data.ghostPos[i].x += moveOffset;
						data.ghostDir[i] = direction.right;
						break;
					case left:
						data.ghostPos[i].x -= moveOffset;
						data.ghostDir[i] = direction.left;
						break;
					default:
						break;
				}
			}
		}
		for(int i = 0; i < 4; i++)
		{
			if(data.frightCounter[i] >= 0)
			{
				if(Ghost.checkCollision(newPos, data.ghostPos[i], nextdir, data.ghostDir[i]))
				{
					if(data.frightCounter[i] > 1)
					{
						data.frightCounter[i] = -1;
						data.ghostPos[i] = new Point(223, 172);
						data.ghostDir[i] = direction.up;
						if(diff > 1 )
						{
							if (counter.stepCounter == 0) 
								counter.stepCounter = depth;
							bonus += ghostScore;
						}
					}
					else
					{
						if (counter.stepGhostCounter == 0) 
							counter.stepGhostCounter = depth;
						return -1000;
					}
				}
			}
		}
		for(int i = 0; i < data.dotCounter; i++)
		{
			if(Dot.checkPointIntersect(data.dots[i], newPos))
			{
				data.dots[i] = data.dots[--data.dotCounter];
				if (counter.stepCounter == 0) 
					counter.stepCounter = depth;
				bonus += dotScore;
				break;
			}
		}
		if (data.dotCounter == 0)
		{
			allDotsEaten = true;
			return (depth+1)*bonus;
		}
		
		if(diff > 1)
		{
			for(int i = 0; i < data.bigDotCounter; i++)
			{
				if(Dot.checkPointIntersect(data.bigDots[i], newPos))
				{
					data.bigDots[i] = data.bigDots[--data.bigDotCounter];
					for(int j = 0; j < 4; j++)
					{
						data.frightCounter[j] = 25;
					}
					bonus += bigdotScore;
					break;
				}
			}
			if(data.fruitItemState && Fruit.eatCheck(newPos))
			{
				bonus += fruitScore;
				data.fruitItemState = false;
			}
		}
		int points, mostPoints = -10000;
		if (depth == 0)
		{
			return bonus;
		}
		GameData copy = GameData.copyGameData(data);
		depthCounter stepscounter = new depthCounter(counter.stepCounter, counter.stepGhostCounter);

		if(nextdir != direction.down && checkAvailability(direction.up, x, y)) 
		{
			points = dirMinMax(direction.up, x, y  - moveOffset, depth-1, copy, stepscounter);
			copy = GameData.copyGameData(data);
		}
		else points = -10000;
		if(points > mostPoints)
		{
			if (counter.stepCounter == 0) 
				savedStepsCounter = stepscounter.stepCounter;
			if (counter.stepGhostCounter == 0) 
				savedStepsGhostCounter = stepscounter.stepGhostCounter;
			mostPoints = points;
		}
		if (!allDotsEaten)
		{
			if(nextdir != direction.up && checkAvailability(direction.down, x, y)) 
			{
				stepscounter.stepCounter = stepscounter.stepGhostCounter  = 0;
				points = dirMinMax(direction.down, x, y+ moveOffset, depth-1, copy, stepscounter);
				copy = GameData.copyGameData(data);
			}
			else points = -10000;
			if(points > mostPoints)
			{
				if (counter.stepCounter == 0) 
					savedStepsCounter = stepscounter.stepCounter;
				if (counter.stepGhostCounter == 0) 
					savedStepsGhostCounter = stepscounter.stepGhostCounter;
				mostPoints = points;
			}
			if (!allDotsEaten)
			{
				if(nextdir != direction.right && checkAvailability(direction.left, x, y))
				{
					stepscounter.stepCounter = stepscounter.stepGhostCounter  = 0;
					if(x < 16 && y > 215 && y < 225) points = dirMinMax(direction.left, 431, y, depth-1, copy, stepscounter);
					else points = dirMinMax(direction.left, x - moveOffset, y, depth-1, copy, stepscounter);
					copy = GameData.copyGameData(data);
				}
				else points = -10000;
				if(points > mostPoints)
				{
					if (counter.stepCounter == 0) 
						savedStepsCounter = stepscounter.stepCounter;
					if (counter.stepGhostCounter == 0) 
						savedStepsGhostCounter = stepscounter.stepGhostCounter;
					mostPoints = points;
				}
				if (!allDotsEaten)
				{
					if(nextdir != direction.left && checkAvailability(direction.right, x, y)) 
					{
						stepscounter.stepCounter = stepscounter.stepGhostCounter  = 0;
						if(x + moveOffset > 431 && y > 215  && y < 225) points = dirMinMax(direction.right, 16 , y, depth-1, copy, stepscounter);
						else points = dirMinMax(direction.right, x + moveOffset, y, depth-1, copy, stepscounter);
						copy = GameData.copyGameData(data);
					}
					else points = -10000;
					if(points > mostPoints)
					{
						if (counter.stepCounter == 0) 
							savedStepsCounter = stepscounter.stepCounter;
						if (counter.stepGhostCounter == 0) 
							savedStepsGhostCounter = stepscounter.stepGhostCounter;
						mostPoints = points;
					}
				}
			}
		}
		if (mostPoints == -10000) mostPoints = 0;
		else
		{
			if (counter.stepCounter == 0) 
				counter.stepCounter = savedStepsCounter;
			if (counter.stepGhostCounter == 0) 
				counter.stepGhostCounter = savedStepsGhostCounter;
		}
		mostPoints += (depth+1)*bonus;
		return mostPoints;
	}
	
}
