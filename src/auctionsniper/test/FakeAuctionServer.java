package auctionsniper.test;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.main.Main;
import auctionsniper.main.SingleMessageListener;
import auctionsniper.xmpp.XMPPAuctionException;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

public class FakeAuctionServer
{
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_RESOURCE = "Auction";
	private static final String AUCTION_PASSWORD = "auction";
	
	private final String itemId;
	private final XMPPConnection connection;
	private Chat currentChat;
	private final SingleMessageListener messageListener = new SingleMessageListener();
	
	public FakeAuctionServer(String itemId)
	{
		this.itemId = itemId;
		this.connection = new XMPPConnection(Main.XMPP_HOSTNAME);
	}
	
	public void startSellingItem() throws XMPPAuctionException
	{
		try
		{
			connection.connect();
			connection.login(String.format(ITEM_ID_AS_LOGIN, itemId),
					AUCTION_PASSWORD, AUCTION_RESOURCE);
			connection.getChatManager().addChatListener(
					new ChatManagerListener()
					{
						public void chatCreated(Chat chat, boolean createdLocally)
						{
							currentChat = chat;
							chat.addMessageListener(messageListener);
						}
					});
		} catch (XMPPException e) {
			throw new XMPPAuctionException("Could not start selling item " + itemId, e);
		}
	}
	
	public void announceClosed() throws XMPPException
	{
		currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
	}
	
	public void reportPrice(int price, int increment, String bidder) throws XMPPException
	{
		currentChat.sendMessage(
				String.format("SOLVersion: 1.1; Event: PRICE; " +
							  "CurrentPrice: %d; Increment: %d; Bidder: %s;",
							  price, increment, bidder));
	}
	
	public void hasReceivedJoinRequestFrom(String sniperId) throws InterruptedException
	{
		receivesAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT));
	}
	
	public void hasReceivedBid(int bid, String sniperId) throws InterruptedException
	{
		receivesAMessageMatching(sniperId, equalTo(String.format(Main.BID_COMMAND_FORMAT, bid)));
	}
	
	private void receivesAMessageMatching(String sniperId, Matcher <? super String> messageMatcher)
		throws InterruptedException
	{
		messageListener.receivesAMessage(messageMatcher);
		assertThat(currentChat.getParticipant(), equalTo(sniperId));
	}
	
	public void stop()
	{
		connection.disconnect();
	}
	
	public String getItemId()
	{
		return itemId;
	}
	
	public void sendInvalidMessageContaining(String message) throws XMPPException
	{
		currentChat.sendMessage(message);
	}
}