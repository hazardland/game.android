package hazardland.lib.game;

public class Target
{
	public static final int CLASS = 0;
	public static final int SUBJECT = 1;
	int type;
	int subject;

	public Target (int subject)
	{
		this.type = SUBJECT;
		this.subject = subject;
	}
	
	public Target (int type, int subject)
	{
		this.type = type;
		this.subject = subject;
	}
}
