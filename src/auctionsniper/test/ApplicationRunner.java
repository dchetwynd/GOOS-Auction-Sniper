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
	
	public void startBiddingIn(final FakeAuctionServer...auctions)
	{
		startSniper();
		
		for (FakeAuctionServer auction: auctions)
		{
			final String itemId = auction.getItemId();
			driver.startBiddingFor(itemId);
			driver.showsSniperStatus(itemId, 0, 0, textFor(SniperState.JOINING));
		}
	}

	private void startSniper() {
		Thread thread = new Thread("Test Application")
		{
			@Override
			public void run()
			{
				try
				{
					Main.main(arguments());
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
	}
	
	protected static String[] arguments(FakeAuctionServer...auctions)
	{
		String[] arguments = new String[auctions.length + 3];
		arguments[0] = Main.XMPP_HOSTNAME;
		arguments[1] = SNIPER_ID;
		arguments[2] = SNIPER_PASSWORD;
		
		for (int i = 0; i < auctions.length; i++)
		{
			arguments[i + 3] = auctions[i].getItemId();
		}
		
		return arguments;
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