package auctionsniper.test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;

import com.objogate.wl.swing.probe.ValueMatcherProbe;

import auctionsniper.main.SnipersTableModel;
import auctionsniper.main.UserRequestListener;
import auctionsniper.ui.MainWindow;

public class MainWindowTest
{
	private final SnipersTableModel tableModel = new SnipersTableModel();
	private final MainWindow mainWindow = new MainWindow(tableModel);
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
