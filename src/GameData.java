import java.awt.Point;

public class GameData 
{
	public Point[] dots;
	public int dotCounter;
	public Point[] bigDots;
	public int bigDotCounter;
	public Point[] ghostPos = new Point[4];
	public MovingObject.direction[] ghostDir = new MovingObject.direction[4];
	public int[] frightCounter = new int[4];
	public boolean fruitItemState;
	public Point pac;
	public MovingObject.direction pacDir;
	
	public GameData()
	{	
	}
	
	public void updateData(Point[] dots, int dotCounter, Point[] bigDots, int bigDotCounter, Point[] ghostPos, MovingObject.direction[] ghostDir, int[] frightCounter, boolean fruitItemState, Point pac,  MovingObject.direction pacDir)
	{
		this.dotCounter = dotCounter;
		this.dots = new Point[dotCounter];
		for(int i = 0; i < dotCounter; i++) this.dots[i] = dots[i];
		this.bigDotCounter = bigDotCounter;
		this.bigDots = new Point[bigDotCounter];
		for(int i = 0; i < bigDotCounter; i++) this.bigDots[i] = bigDots[i];
		for(int i = 0; i < 4; i++) 
		{
			this.ghostPos[i] = new Point(ghostPos[i].x, ghostPos[i].y);
			this.ghostDir[i] = ghostDir[i];
			this.frightCounter[i] = frightCounter[i];
		}
		this.fruitItemState = fruitItemState;
		this.pac = new Point(pac.x, pac.y);
		this.pacDir = pacDir;
	}
	
	public static GameData copyGameData(GameData source)
	{
		GameData copy = new GameData();
		copy.updateData(source.dots, source.dotCounter, source.bigDots, source.bigDotCounter, source.ghostPos, source.ghostDir, source.frightCounter, source.fruitItemState, source.pac, source.pacDir);
		return copy;
	}
}
