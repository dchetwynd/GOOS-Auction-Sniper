package auctionsniper.main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.ui.MainWindow;

public class Main {
	private static final int ARG_HOSTNAME = 0;
	private static final int ARG_USERNAME = 1;
	private static final int ARG_PASSWORD = 2;
	private static final int ARG_ITEM_ID = 3;
	
	public static final String AUCTION_RESOURCE = "Auction";
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN +  "@%s/" + AUCTION_RESOURCE;
	public static final String XMPP_HOSTNAME = "127.0.0.1";
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
	
	private final SnipersTableModel snipers = new SnipersTableModel();
	private MainWindow ui;
	
	@SuppressWarnings("unused")
	private Chat notToBeGcd;
	
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
		main.joinAuction(
				connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]),
				args[ARG_ITEM_ID]);
	}
	
	private void joinAuction(XMPPConnection connection, String itemId)
		throws XMPPException
	{
		disconnectWhenUICloses(connection);
		final Chat chat = connection.getChatManager().createChat(auctionId(itemId, connection), null);
		this.notToBeGcd = chat;
		
		Auction auction = new XMPPAuction(chat);
		chat.addMessageListener(
				new AuctionMessageTranslator(
						connection.getUser(),
						new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers))));
		auction.join();
	}
	
	private void disconnectWhenUICloses(final XMPPConnection connection)
	{
		ui.addWindowListener(new WindowAdapter()
							{
								@Override
								public void windowClosed(WindowEvent e)
								{
									connection.disconnect();
								}
							});
	}
	
	private static XMPPConnection connection(String hostname, String username, String password)
			throws XMPPException
	{
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);
		
		return connection;
	}
	
	private static String auctionId(String itemId, XMPPConnection connection)
	{
		return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}
}