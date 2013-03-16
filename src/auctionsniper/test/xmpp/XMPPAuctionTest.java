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
import auctionsniper.main.Item;
import auctionsniper.main.Main;
import auctionsniper.test.ApplicationRunner;
import auctionsniper.test.FakeAuctionServer;
import auctionsniper.xmpp.XMPPAuction;
import auctionsniper.xmpp.XMPPAuctionException;
import auctionsniper.xmpp.XMPPAuctionHouse;

public class XMPPAuctionTest
{
	private final String ITEM_ID = "item-54321";
	private final FakeAuctionServer auctionServer = new FakeAuctionServer(ITEM_ID);
	private XMPPAuctionHouse auctionHouse;
	
	@Before
	public void startSellingItem() throws XMPPAuctionException
	{
		auctionHouse = XMPPAuctionHouse.connect(Main.XMPP_HOSTNAME,
											   ApplicationRunner.SNIPER_ID,
											   ApplicationRunner.SNIPER_PASSWORD);
		auctionServer.startSellingItem();
	}
	
	@After
	public void closeConnection()
	{
		if (auctionHouse != null)
			auctionHouse.disconnect();
	}
	
	@Test
	public void receivesEventsFromAuctionServerAfterJoining() throws Exception
	{
		CountDownLatch auctionWasClosed = new CountDownLatch(1);
		Item item = new Item(ITEM_ID, Integer.MAX_VALUE);
		
		Auction auction = auctionHouse.auctionFor(item);
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
					   public void auctionFailed() {};
				   };
	}
}