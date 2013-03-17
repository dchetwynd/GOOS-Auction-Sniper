package auctionsniper.test;

import java.io.IOException;

import javax.swing.SwingUtilities;

import auctionsniper.main.AuctionLogDriver;
import auctionsniper.main.Main;
import auctionsniper.main.SniperState;
import auctionsniper.ui.MainWindow;
import auctionsniper.xmpp.XMPPAuctionHouse;

import static auctionsniper.main.SnipersTableModel.textFor;
import static org.hamcrest.Matchers.containsString;

public class ApplicationRunner {
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	public static String SNIPER_XMPP_ID = String.format("%s@%s/%s",
			SNIPER_ID, Main.XMPP_HOSTNAME, XMPPAuctionHouse.AUCTION_RESOURCE);
	
	private AuctionSniperDriver driver;
	private AuctionLogDriver logDriver = new AuctionLogDriver();
	
	public void startBiddingIn(final FakeAuctionServer...auctions)
	{
		startSniper();
		for (FakeAuctionServer auction: auctions)
		{
			openBiddingFor(Integer.MAX_VALUE, auction);
		}
	}

	private void startSniper()
	{
		logDriver.clearLog();
		Thread thread = new Thread("Test Application")
		{
			@Override
			public void run()
			{
				try
				{
					Main.main(Main.XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
		makeSureAwtIsLoadedBeforeStartingTheDriverOnOSXToStopDeadlock();
		
		driver = new AuctionSniperDriver(1000);
		driver.hasTitle(MainWindow.APPLICATION_TITLE);
		driver.hasColumnTitles();
	}
	
	public void showsSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid)
	{
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.LOST));
	}
	
	public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice)
	{
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, textFor(SniperState.WON));
	}
	
	public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid)
	{
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.BIDDING));
	}
	
	public void hasShownSniperIsLosing(FakeAuctionServer auction, int lastPrice, int lastBid)
	{
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.LOSING));
	}
	
	public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid)
	{
		driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, textFor(SniperState.WINNING));
	}
	
	public void startBiddingWithStopPrice(int stopPrice, final FakeAuctionServer auction)
	{
		startSniper();
		openBiddingFor(stopPrice, auction);
	}

	private void openBiddingFor(int stopPrice, final FakeAuctionServer auction) {
		final String itemId = auction.getItemId();
		driver.startBiddingFor(itemId, stopPrice);
		driver.showsSniperStatus(itemId, 0, 0, textFor(SniperState.JOINING));
	}

	private void makeSureAwtIsLoadedBeforeStartingTheDriverOnOSXToStopDeadlock()
	{
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
										{
											public void run() {}
										});
		} catch (Exception e){
			throw new AssertionError(e);
		}
	}
	
	public void stop()
	{
		if (driver != null)
		{
			driver.dispose();
		}
	}
	
	public void showsSniperHasFailed(FakeAuctionServer auction)
	{
		driver.showsSniperStatus(auction.getItemId(), 0, 0, textFor(SniperState.FAILED));
	}
	
	public void reportsInvalidMessage(FakeAuctionServer auction, String invalidMessage)
		throws IOException
	{
		logDriver.hasEntry(containsString(invalidMessage));
	}
}