package auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import auctionsniper.main.Announcer;
import auctionsniper.main.SnipersTableModel;
import auctionsniper.main.UserRequestListener;

public class MainWindow extends JFrame
{
	public static final String SNIPER_STATUS_NAME = "sniper status";
	public static final String APPLICATION_TITLE = "Auction Sniper";
	public static final String NEW_ITEM_ID_NAME = "New Item Id";
	public static final String JOIN_BUTTON_NAME = "Join Button";
	
	private final String SNIPERS_TABLE_NAME = "Snipers";
	private final Announcer<UserRequestListener> userRequests =
		Announcer.to(UserRequestListener.class);
	
	public MainWindow(SnipersTableModel snipers)
	{
		super("Auction Sniper");
		setName(APPLICATION_TITLE);
		fillContentPane(makeControls(), makeSnipersTable(snipers));
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void addUserRequestListener(UserRequestListener listener)
	{
		userRequests.addListener(listener);
	}
	
	private void fillContentPane(JPanel controls, JTable snipersTable)
	{
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		contentPane.add(controls, BorderLayout.NORTH);
		contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
	}
	
	private JTable makeSnipersTable(SnipersTableModel snipers)
	{
		final JTable snipersTable = new JTable(snipers);
		snipersTable.setName(SNIPERS_TABLE_NAME);
		return snipersTable;
	}
	
	private JPanel makeControls()
	{
		JPanel controls = new JPanel(new FlowLayout());
		
		final JTextField itemIdField = new JTextField();
		itemIdField.setColumns(25);
		itemIdField.setName(NEW_ITEM_ID_NAME);
		controls.add(itemIdField);
		
		JButton joinAuctionButton = new JButton("Join Auction");
		joinAuctionButton.setName(JOIN_BUTTON_NAME);
		joinAuctionButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						userRequests.announce().joinAuction(itemIdField.getText());
					}
				});
		controls.add(joinAuctionButton);
		
		return controls;
	}
}
