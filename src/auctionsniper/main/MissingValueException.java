package auctionsniper.main;

public class MissingValueException extends Exception
{
	private String valueName;
	
	public MissingValueException(String valueName)
	{
		this.valueName = valueName;
	}
}
