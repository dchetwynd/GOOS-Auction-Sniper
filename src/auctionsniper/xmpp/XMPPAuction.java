package auctionsniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.main.Announcer;
import auctionsniper.main.Auction;
import auctionsniper.main.AuctionEventListener;
import auctionsniper.main.Main;
import auctionsniper.main.AuctionEventListener.PriceSource;

public class XMPPAuction implements Auction
{
	private final Announcer<AuctionEventListener> auctionEventListeners =
		Announcer.to(AuctionEventListener.class);
	private final Chat chat;
	private final XMPPFailureReporter failureReporter;
	
	public XMPPAuction(XMPPConnection connection, String itemId,
		XMPPFailureReporter failureReporter)
	{
		this.failureReporter = failureReporter;
		AuctionMessageTranslator translator = translatorFor(connection);
		this.chat = connection.getChatManager()
						.createChat(auctionId(itemId, connection), translator);
		addAuctionEventListener(chatDisconnectFor(translator));
	}
	
	public void bid(int amount)
	{
		sendMessage(String.format(Main.BID_COMMAND_FORMAT, amount));
	}
	
	public void join()
	{
		sendMessage(Main.JOIN_COMMAND_FORMAT);
	}
	
	public void addAuctionEventListener(AuctionEventListener listener)
	{
		auctionEventListeners.addListener(listener);
	}
	
	private AuctionEventListener chatDisconnectFor(final AuctionMessageTranslator translator)
	{
		return new AuctionEventListener()
		{
			public void auctionFailed()
			{
				chat.removeMessageListener(translator);
			}
			public void auctionClosed() {}
			public void currentPrice(int price, int increment, PriceSource bidder) {}
		};
	}
	
	private AuctionMessageTranslator translatorFor(XMPPConnection connection)
	{
		return new AuctionMessageTranslator(
			connection.getUser(),
			auctionEventListeners.announce(),
			failureReporter);
	}
	
	private void sendMessage(final String message)
	{
		try
		{
			chat.sendMessage(message);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}
	
	private static String auctionId(String itemId, XMPPConnection connection)
	{
		return String.format(XMPPAuctionHouse.AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}
}
