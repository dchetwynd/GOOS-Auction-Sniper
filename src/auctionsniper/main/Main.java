package auctionsniper.main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.ui.MainWindow;
import auctionsniper.xmpp.XMPPAuction;
import auctionsniper.xmpp.XMPPAuctionHouse;

public class Main {
	private static final int ARG_HOSTNAME = 0;
	private static final int ARG_USERNAME = 1;
	private static final int ARG_PASSWORD = 2;
	
	public static final String XMPP_HOSTNAME = "127.0.0.1";
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
	
	private final SnipersTableModel snipers = new SnipersTableModel();
	private MainWindow ui;
	
	private static List<Auction> notToBeGcd = new ArrayList<Auction>();
	
	public Main() throws Exception
	{
		SwingUtilities.invokeAndWait(new Runnable()
		{
			public void run() { ui = new MainWindow(snipers); }
		});
	}
	
	public static void main(String...args) throws Exception
	{
		Main main = new Main();
		XMPPAuctionHouse auctionHouse =
			XMPPAuctionHouse.connect(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
		main.disconnectWhenUICloses(auctionHouse);
		main.addUserRequestListenerFor(auctionHouse);
	}
	
	private void addUserRequestListenerFor(final AuctionHouse auctionHouse)
	{
		ui.addUserRequestListener(
			new SniperLauncher(auctionHouse, snipers));
	}
	
	private void disconnectWhenUICloses(final XMPPAuctionHouse auctionHouse)
	{
		ui.addWindowListener(new WindowAdapter()
							{
								@Override
								public void windowClosed(WindowEvent e)
								{
									auctionHouse.disconnect();
								}
							});
	}
}
