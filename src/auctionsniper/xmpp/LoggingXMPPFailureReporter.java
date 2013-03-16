package auctionsniper.xmpp;

import java.util.logging.Logger;

import auctionsniper.main.LogWrapper;

public class LoggingXMPPFailureReporter implements XMPPFailureReporter
{
	private LogWrapper logger;
	
	public LoggingXMPPFailureReporter(LogWrapper logger)
	{
		this.logger = logger;
	}
	
	public void cannotTranslateMessage(String auctionId, String failedMessage,
		Exception exception)
	{
		String loggingMessage = "<" + auctionId + "> Could not translate message \"" +
			failedMessage + "\" because \""  + exception + "\"";
		logger.severe(loggingMessage);
	}
}
