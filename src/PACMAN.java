import java.awt.AWTException;
import java.awt.Color;
import java.awt.FontFormatException;
import java.io.IOException;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class PACMAN extends JFrame
{
	public PACMAN() throws IOException, AWTException, FontFormatException
	{
		setTitle("PACMAN By Avishay Gal");
		setBounds(0, 0, 462, 724);
		setBackground(Color.BLACK);
		setResizable(false);
		

		Painter picasso = new Painter();
		add(picasso);
		
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		picasso.addKeyListener(picasso);
	}
	
	public static void main(String[] args)
	{
		
		try {
			new PACMAN();
		} 
		catch (IOException | AWTException | FontFormatException e) 
		{
			Painter.errorDialog(e);
		}

	}

}
