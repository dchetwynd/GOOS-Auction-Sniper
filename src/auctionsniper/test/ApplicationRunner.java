package auctionsniper.test;

import auctionsniper.main.Main;
import auctionsniper.main.SniperState;
import auctionsniper.ui.MainWindow;
import static auctionsniper.main.SnipersTableModel.textFor;

public class ApplicationRunner {
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	public static String SNIPER_XMPP_ID = String.format("%s@%s/%s",
			SNIPER_ID, Main.XMPP_HOSTNAME, Main.AUCTION_RESOURCE);
	
	private AuctionSniperDriver driver;
	
	public void startBiddingIn(final FakeAuctionServer auction)
	{
		Thread thread = new Thread("Test Application")
		{
			@Override
			public void run()
			{
				try
				{
					Main.main(Main.XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
		driver = new AuctionSniperDriver(1000);
		driver.hasTitle(MainWindow.APPLICATION_TITLE);
		driver.hasColumnTitles();
		driver.showsSniperStatus(auction.getItemId(), 0, 0, textFor(SniperState.JOINING));
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
	
	public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid)
	{
		driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, textFor(SniperState.WINNING));
	}
	
	public void stop()
	{
		if (driver != null)
		{
			driver.dispose();
		}
	}
}