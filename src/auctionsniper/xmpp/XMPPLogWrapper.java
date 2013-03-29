package auctionsniper.xmpp;

import java.util.logging.Logger;

public class XMPPLogWrapper implements LogWrapper
{
	private Logger logger;
	
	public XMPPLogWrapper(Logger logger)
	{
		this.logger = logger;
	}
	
	public void severe(String logMessage)
	{
		logger.severe(logMessage);
	}
}
