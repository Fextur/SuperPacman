import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.Point;
import java.io.IOException;
import java.util.Random;

@SuppressWarnings("serial")
public abstract class Ghost extends MovingObject
{
	enum Mode {scatter, chase, freight, eaten};
	protected String color;
	public int frightCounter = 0;
	private boolean toFlip = false;
	public Mode mode;
	protected Point scatterCorner;
	public long startTime = -1000;
	protected Point pac = new Point();
	public direction pacDir;
	public boolean collided = false;
	
	public Ghost(String name, int x, int y, int level, Point sc, int[] releventKeys) throws IOException 
	{
		super(x ,y , name, 75, level, releventKeys);
		color = name;
		ai = true;
		mode = Mode.scatter;
		scatterCorner = sc;
	}

	abstract Point findChaseTarget();
	
	private void updateMode()
	{
		if(mode == Mode.eaten)
		{
			if(inGhostBlock() && pos.y > 214)
			{
				mode = Mode.scatter;
				frightCounter = 0;
			}
		}
		else if(frightCounter == 0)
		{
			long timeDelta = (System.currentTimeMillis() - startTime) % 27000;
			int repeat = (int) ((System.currentTimeMillis() - startTime) / 27000);
			speed = 75;
			levelDelayBonus();
			Directory = color;
			if(repeat > 3 || timeDelta > 7000)
			{
				if(mode != Mode.chase) toFlip = true;
				mode = Mode.chase;
			}
			else 
			{

				if(mode != Mode.scatter) toFlip = true;
				mode = Mode.scatter;
			}
		}
		else
		{	
			frightCounter--;
			if(frightCounter < 8)
			{
				if(Directory == "Blue") Directory = "White";
				else Directory = "Blue";
			}
		}
	}
	
	@Override
	public void updatePos() throws AWTException 
	{
		if(Lose) return;
		if(collided)
		{
			if(mode == Mode.freight) mode = Mode.eaten;
			else if(mode != Mode.eaten) Lose = true;
			collided = false;
			keyCode = 0;
		}
		else updateMode();
		if(ai) 
		{
			if(inGhostBlock() && mode != Mode.eaten)
			{
				moveOffset = 10;
				dir = targetDirection(new Point(216, 142));
			}
			else if(toFlip)
			{
				toFlip = false;
				if(dir == direction.up && checkAvailability(direction.down)) dir = direction.down;
				else if(dir == direction.down && checkAvailability(direction.up)) dir = direction.up;
				else if(dir == direction.left && checkAvailability(direction.right)) dir = direction.right;
				else if(dir == direction.right && checkAvailability(direction.left)) dir = direction.left;
				else if(!checkAvailability(dir)) dir  = targetDirection(pac);
				
			}
			else switch(mode)
			{
				case scatter:
				{
					dir = targetDirection(scatterCorner);
					break;
				}
				case chase:
				{
					dir = targetDirection(findChaseTarget());
					break;
				}
				case freight:
				{
					dir = freightDirection();
					break;
				}
				case eaten:
				{
					speed = 150;
					Directory = "Eyes";
					moveOffset = 10;
					frightCounter = -1;
					dir = targetDirection(new Point(216, 216));
					break;
				}
			}
		}
		else 
		{
			if(mode == Mode.eaten)
			{
				speed = 150;
				Directory = "Eyes";
				moveOffset = 10;
				frightCounter = -1;
				dir = targetDirection(new Point(216, 216));
			}
			else
			{
				direction nextDir = FindDirectionFromInput();
				if(diff > 1)
				{
					if(inGhostBlock()) dir = nextDir;
					else switch(nextDir)
					{
						case up:
							if(dir != direction.down) dir = nextDir;
							break;
						case down:
							if(dir != direction.up) dir = nextDir;
							break;
						case left:
							if(dir != direction.right) dir = nextDir;
							break;
						case right:
							if(dir != direction.left) dir = nextDir;
							break;
					default:
						break;
					}
				}
				else dir = nextDir;
				if(!checkAvailability(dir)) return;
			}
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
		moveOffset = 16;
	}
	
	private direction freightDirection() throws AWTException
	{
		direction[] possibleDirections = new direction[4];
		int count = 0;
		if(dir != direction.down && checkAvailability(direction.up)) possibleDirections[count++] = direction.up;
		if(dir != direction.up && checkAvailability(direction.down)) possibleDirections[count++] = direction.down;
		if(dir != direction.left && checkAvailability(direction.right)) possibleDirections[count++] = direction.right;
		if(dir != direction.right && checkAvailability(direction.left)) possibleDirections[count++] = direction.left;
		Random random = new Random();
		if(count == 0)
		{
			if(checkAvailability(direction.up)) possibleDirections[count++] = direction.up;
			if(checkAvailability(direction.down)) possibleDirections[count++] = direction.down;
			if(checkAvailability(direction.right)) possibleDirections[count++] = direction.right;
			if(checkAvailability(direction.left)) possibleDirections[count++] = direction.left;
			
		}
		return possibleDirections[random.nextInt(count)];
	}
	
	public void setFreightMode()
	{
		if(inGhostBlock()) return;
		toFlip = true;
		speed = 50;
		if(level >= 2 && level <= 4) speed += 5;
		if(level >= 5) speed += 5;
		frightCounter = 25;
		Directory = "Blue";
		mode = Mode.freight;
	}
	
	public boolean checkCollision()
	{
		if (mode == Mode.eaten) return false;
		return checkCollision(pac, pos, pacDir, dir);
	}
	
	public static boolean checkCollision(Point pac, Point pos, direction pacDir, direction dir)
	{
		if(pac.distance(pos) > 16) return false;
		if(pac.distance(pos) == 0) return true;
		if(pac.x == pos.x)
		{
			if(pos.y - pac.y == 16 && dir == direction.down && pacDir == direction.up) return true;
			if(pac.y - pos.y == 16 && dir == direction.up && pacDir == direction.down) return true;
		}
		if(pac.y == pos.y)
		{
			if(pos.x - pac.x == 16 && dir == direction.right && pacDir == direction.left) return true;
			if( pac.x - pos.x == 16 && dir == direction.left && pacDir == direction.right) return true;
		}
		return false;
	}
	
    public void Lost(Graphics g)
	{
		sprite.paintIcon(this, g, 1000, 1000);
	}

	public void setPacPos(Point pacPos)
	{
		pac.x = pacPos.x;
		pac.y = pacPos.y;
	}
	
	
}
