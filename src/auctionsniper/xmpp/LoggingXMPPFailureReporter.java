package auctionsniper.xmpp;

public class LoggingXMPPFailureReporter implements XMPPFailureReporter
{
	private final LogWrapper logger;
	
	public LoggingXMPPFailureReporter(LogWrapper logger)
	{
		this.logger = logger;
	}
	
	public void cannotTranslateMessage(String auctionId, String failureMessage,
		Exception exception)
	{
		logger.severe(String.format("<%s> Could not translate message \"%s\" " +
				"because \"%s\"", auctionId, failureMessage, exception));
	}
}
