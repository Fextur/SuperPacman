import java.io.FileNotFoundException;

@SuppressWarnings("serial")
public class Score extends TextObject
{
	int points = 0;
	public Score() throws FileNotFoundException
	{
		super(110, 90);
	}

	@Override
	protected String definePrint() 
	{
		return points + "";		
	}
	
}
