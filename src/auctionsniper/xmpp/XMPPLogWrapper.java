package auctionsniper.xmpp;

import java.util.logging.Logger;

import auctionsniper.main.LogWrapper;

public class XMPPLogWrapper implements LogWrapper
{
	private Logger logger;
	
	public XMPPLogWrapper(Logger logger)
	{
		this.logger = logger;
	}
	
	public void severe(String errorMessage)
	{
		logger.severe(errorMessage);
	}
}
