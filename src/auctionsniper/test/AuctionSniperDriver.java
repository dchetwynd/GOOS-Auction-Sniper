package auctionsniper.test;

import javax.swing.table.JTableHeader;

import auctionsniper.ui.MainWindow;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;

import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import com.objogate.wl.swing.gesture.GesturePerformer;
import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.*;
import static java.lang.String.valueOf;

public class AuctionSniperDriver extends JFrameDriver {
	public AuctionSniperDriver(int timeoutMillis)
	{
		super(new GesturePerformer(),
				JFrameDriver.topLevelFrame(
						named(MainWindow.APPLICATION_TITLE),
						showingOnScreen()),
				new AWTEventQueueProber(timeoutMillis, 100));
	}
	
	public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String statusText)
	{
		JTableDriver table = new JTableDriver(this);
		table.hasRow(
				matching(withLabelText(itemId), withLabelText(valueOf(lastPrice)),
						 withLabelText(valueOf(lastBid)), withLabelText(statusText)));
	}
	
	public void hasColumnTitles()
	{
		JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
		headers.hasHeaders(matching(withLabelText("Item"), withLabelText("Last Price"),
									withLabelText("Last Bid"), withLabelText("State")));
	}
}
