package auctionsniper.xmpp;

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
	
	private XMPPConnection connection;
	
	public XMPPAuctionHouse(XMPPConnection connection)
	{
		this.connection = connection;
	}
	
	public Auction auctionFor(Item item)
	{
		return new XMPPAuction(connection, item.identifier);
	}
	
	public XMPPConnection getConnection()
	{
		return connection;
	}
	
	public static XMPPAuctionHouse connect(String hostname, String username, String password)
		throws XMPPException
	{
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);
		
		return new XMPPAuctionHouse(connection);
	}
	
	public void disconnect()
	{
		if (connection != null)
		{
			connection.disconnect();
		}
	}
}