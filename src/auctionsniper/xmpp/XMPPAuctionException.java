package auctionsniper.xmpp;

public class XMPPAuctionException extends Exception
{
	private final String errorMessage;
	private final Exception sourceException;
	
	public XMPPAuctionException(String errorMessage, Exception exception)
	{
		this.errorMessage = errorMessage;
		this.sourceException = exception;
	}
}
