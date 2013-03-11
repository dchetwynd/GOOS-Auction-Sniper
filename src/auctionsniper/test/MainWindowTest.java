package auctionsniper.test;

import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;

import com.objogate.wl.swing.probe.ValueMatcherProbe;

import auctionsniper.main.Item;
import auctionsniper.main.SniperPortfolio;
import auctionsniper.main.SnipersTableModel;
import auctionsniper.main.UserRequestListener;
import auctionsniper.ui.MainWindow;

public class MainWindowTest
{
	private final SniperPortfolio portfolio = new SniperPortfolio();
	private final MainWindow mainWindow = new MainWindow(portfolio);
	private final AuctionSniperDriver driver = new AuctionSniperDriver(100);
	
	@Test
	public void makesUserRequestWhenJoinButtonClicked()
	{
		final String itemId = "item-54321";
		final ValueMatcherProbe<Item> itemProbe = 
			new ValueMatcherProbe<Item>(equalTo(new Item(itemId, 789)), "join request");
		
		mainWindow.addUserRequestListener(
			new UserRequestListener()
			{
				public void joinAuction(Item item)
				{
					itemProbe.setReceivedValue(item);
				}
			});
		
		driver.startBiddingFor(itemId, 789);
		driver.check(itemProbe);
	}
}
