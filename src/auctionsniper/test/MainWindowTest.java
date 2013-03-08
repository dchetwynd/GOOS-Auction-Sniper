package auctionsniper.test;

import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;

import com.objogate.wl.swing.probe.ValueMatcherProbe;

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
		final ValueMatcherProbe<String> buttonProbe = 
			new ValueMatcherProbe<String>(equalTo(itemId), "join request");
		
		mainWindow.addUserRequestListener(
			new UserRequestListener()
			{
				public void joinAuction(String itemId)
				{
					buttonProbe.setReceivedValue(itemId);
				}
			});
		
		driver.startBiddingFor(itemId);
		driver.check(buttonProbe);
	}
}
