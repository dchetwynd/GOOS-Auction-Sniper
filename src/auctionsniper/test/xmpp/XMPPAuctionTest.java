package auctionsniper.test.xmpp;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import auctionsniper.main.Auction;
import auctionsniper.main.AuctionEventListener;
import auctionsniper.main.Main;
import auctionsniper.test.ApplicationRunner;
import auctionsniper.test.FakeAuctionServer;
import auctionsniper.xmpp.XMPPAuction;

public class XMPPAuctionTest
{
	private final String ITEM_ID = "item-54321";
	private final FakeAuctionServer auctionServer = new FakeAuctionServer(ITEM_ID);
	private XMPPConnection connection;
	
	@Before
	public void startSellingItem() throws XMPPException
	{
		connection = Main.connection(Main.XMPP_HOSTNAME, ApplicationRunner.SNIPER_ID,
				ApplicationRunner.SNIPER_PASSWORD);
		auctionServer.startSellingItem();
	}
	
	@After
	public void closeConnection()
	{
		if (connection != null)
			connection.disconnect();
	}
	
	@Test
	public void receivesEventsFromAuctionServerAfterJoining() throws Exception
	{
		CountDownLatch auctionWasClosed = new CountDownLatch(1);
		
		Auction auction = new XMPPAuction(connection, auctionServer.getItemId());
		auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));
		
		auction.join();
		
		auctionServer.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
		auctionServer.announceClosed();
		
		assertTrue("Should have been closed", auctionWasClosed.await(2, TimeUnit.SECONDS));
	}
	
	private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed)
	{
		return new AuctionEventListener()
				   {
					   public void auctionClosed() { auctionWasClosed.countDown(); }
					   public void currentPrice(int price, int increment, PriceSource priceSource) {}
				   };
	}

}
