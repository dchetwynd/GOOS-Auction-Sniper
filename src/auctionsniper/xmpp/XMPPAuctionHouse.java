package auctionsniper.xmpp;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.FilenameUtils;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.main.Auction;
import auctionsniper.main.AuctionHouse;
import auctionsniper.main.Item;

public class XMPPAuctionHouse implements AuctionHouse
{
	public static final String AUCTION_RESOURCE = "Auction";
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN +  "@%s/" + AUCTION_RESOURCE;
	public static final String LOG_FILE_NAME = "auction-sniper.log";
	
	private static final String LOGGER_NAME = "auction-sniper";
	private XMPPConnection connection;
	private final LoggingXMPPFailureReporter failureReporter;
	
	public XMPPAuctionHouse(XMPPConnection connection) throws XMPPAuctionException
	{
		this.connection = connection;
		this.failureReporter = new LoggingXMPPFailureReporter(makeLogger());
	}
	
	public Auction auctionFor(Item item)
	{
		return new XMPPAuction(connection, item.identifier, failureReporter);
	}
	
	public XMPPConnection getConnection()
	{
		return connection;
	}
	
	public static XMPPAuctionHouse connect(String hostname, String username, String password)
		throws XMPPAuctionException
	{
		try
		{
			XMPPConnection connection = new XMPPConnection(hostname);
			connection.connect();
			connection.login(username, password, AUCTION_RESOURCE);
			
			return new XMPPAuctionHouse(connection);
		} catch (XMPPException e) {
			throw new XMPPAuctionException("Could not connect to auction", e);
		}
	}
	
	public void disconnect()
	{
		if (connection != null)
		{
			connection.disconnect();
		}
	}
	
	private LogWrapper makeLogger() throws XMPPAuctionException
	{
		Logger logger = Logger.getLogger(LOGGER_NAME);
		logger.setUseParentHandlers(false);
		logger.addHandler(simpleFileHandler());
		return new XMPPLogWrapper(logger);
	}
	
	private FileHandler simpleFileHandler() throws XMPPAuctionException
	{
		try
		{
			FileHandler handler = new FileHandler(LOG_FILE_NAME);
			handler.setFormatter(new SimpleFormatter());
			return handler;
		} catch (Exception e) {
			throw new XMPPAuctionException("Could not create logger FileHandler " +
				FilenameUtils.getFullPath(LOG_FILE_NAME), e);
		}
	}
}