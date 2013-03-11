package auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import auctionsniper.main.Announcer;
import auctionsniper.main.Item;
import auctionsniper.main.SniperPortfolio;
import auctionsniper.main.SnipersTableModel;
import auctionsniper.main.UserRequestListener;

public class MainWindow extends JFrame
{
	public static final String SNIPER_STATUS_NAME = "sniper status";
	public static final String APPLICATION_TITLE = "Auction Sniper";
	public static final String NEW_ITEM_ID_NAME = "New Item ID Name";
	public static final String NEW_ITEM_ID_LABEL = "New Item ID Label";
	public static final String NEW_ITEM_STOP_PRICE_NAME = "New Item Stop Price Name";
	public static final String NEW_ITEM_STOP_PRICE_LABEL = "New Item Stop Price Label";
	public static final String JOIN_BUTTON_NAME = "Join Button";
	
	private JLabel itemIdLabel;
	private JTextField itemIdField;
	private JLabel stopPriceLabel;
	private JFormattedTextField stopPriceField;
	
	private final String SNIPERS_TABLE_NAME = "Snipers";
	private final Announcer<UserRequestListener> userRequests =
		Announcer.to(UserRequestListener.class);
	
	public MainWindow(SniperPortfolio portfolio)
	{
		super("Auction Sniper");
		setName(APPLICATION_TITLE);
		fillContentPane(makeControls(), makeSnipersTable(portfolio));
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
	
	private JTable makeSnipersTable(SniperPortfolio portfolio)
	{
		final SnipersTableModel model = new SnipersTableModel();
		portfolio.addPortfolioListener(model);
		final JTable snipersTable = new JTable(model);
		snipersTable.setName(SNIPERS_TABLE_NAME);
		return snipersTable;
	}
	
	private JPanel makeControls()
	{
		JPanel controls = new JPanel(new FlowLayout());
		
		addItemIdLabel(controls);
		addItemIdField(controls);
		addStopPriceLabel(controls);
		addStopPriceField(controls);
		addJoinAuctionButton(controls);
		
		return controls;
	}

	private void addJoinAuctionButton(JPanel controls) {
		JButton joinAuctionButton = new JButton("Join Auction");
		joinAuctionButton.setName(JOIN_BUTTON_NAME);
		joinAuctionButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						userRequests.announce().joinAuction(
							new Item(itemId(), stopPrice()));
					}
				});
		controls.add(joinAuctionButton);
	}
	
	private String itemId()
	{
		return itemIdField.getText();
	}
	
	private int stopPrice()
	{
		return ((Number)stopPriceField.getValue()).intValue();
	}

	private void addStopPriceField(JPanel controls) {
		stopPriceField = new JFormattedTextField();
		DecimalFormat numericFormat = (DecimalFormat)DecimalFormat.getNumberInstance();
		numericFormat.setMaximumIntegerDigits(10);
		stopPriceField.setFormatterFactory(
			new DefaultFormatterFactory(new NumberFormatter(numericFormat)));
		
		stopPriceField.setColumns(25);
		stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
		controls.add(stopPriceField);
	}

	private void addStopPriceLabel(JPanel controls) {
		stopPriceLabel = new JLabel("Stop Price: ");
		stopPriceLabel.setName(NEW_ITEM_STOP_PRICE_LABEL);
		controls.add(stopPriceLabel);
	}

	private void addItemIdField(JPanel controls) {
		itemIdField = new JTextField();
		itemIdField.setColumns(25);
		itemIdField.setName(NEW_ITEM_ID_NAME);
		controls.add(itemIdField);
	}

	private void addItemIdLabel(JPanel controls) {
		itemIdLabel = new JLabel("Item ID: ");
		itemIdLabel.setName(NEW_ITEM_ID_LABEL);
		controls.add(itemIdLabel);
	}
}
