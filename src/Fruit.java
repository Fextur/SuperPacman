import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Fruit extends JPanel
{
	private ImageIcon fruitImage;
	public Point pac;
	public boolean first = false;
	public boolean second = false;
	public boolean state = false;	
	private String item;
	private int level;
	
	public Fruit(int level)
	{
		this.level = level;
		if(level == 1) item = "Cherry";
		if(level == 2) item = "Strawberry";
		if(level >= 3) item = "Orange";
		if(level >= 5) item = "Apple";
		if(level >= 7) item = "Melon";
		if(level >= 9) item = "GalaxianBoss";
		if(level >= 11) item = "Bell";
		if(level >= 13) item = "Key";
	}
	
	public void drawFruit(Graphics g, int dotCounter) throws FileNotFoundException
	{
		if(dotCounter < 145 && !first)
		{
			first = true;
			state = true;
		}
		if(dotCounter < 75 && !second)
		{
			second = true;
			state = true;
		}
		if(state)
		{
			fruitImage = new ImageIcon("Assets/Sprites/Fruits/"+item+".png");
			if(fruitImage.getIconHeight() < 0)
				throw new FileNotFoundException(item + " not found");
			fruitImage.paintIcon(this, g, 215, 380);
		}
		fruitImage = new ImageIcon("Assets/Sprites/Fruits/Showcase"+item+".png");
		if(fruitImage.getIconHeight() < 0)
			throw new FileNotFoundException("Showcase" + item + " not found");
		fruitImage.paintIcon(this, g, 311, 624);
	}
	
	public int eatCheck()
	{
		if(!state) return 0;
		state = !eatCheck(pac);
		if(!state)
		{
			if(level == 1) return 100;
			if(level == 2) return 300;
			if(level >= 3) return 500;
			if(level >= 5) return 700;
			if(level >= 7) return 1000;
			if(level >= 9) return 2000;
			if(level >= 11) return 3000;
			if(level >= 13) return 5000;
		}
		return 0;
	}
	
	public static boolean eatCheck(Point pos)
	{
		Rectangle fruitRec = new Rectangle(215,380 - 115, 24, 24);
		Rectangle pacRec = new Rectangle(pos.x,pos.y, 24, 24);
		if(fruitRec.intersects(pacRec)) return true;
		return false;
	}
	
}
