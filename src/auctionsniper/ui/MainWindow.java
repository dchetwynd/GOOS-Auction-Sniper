package auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import auctionsniper.main.SnipersTableModel;

public class MainWindow extends JFrame
{
	public static final String SNIPER_STATUS_NAME = "sniper status";
	public static final String APPLICATION_TITLE = "Auction Sniper";
	
	private final String SNIPERS_TABLE_NAME = "Snipers";
	private final SnipersTableModel snipers;
	
	public MainWindow(SnipersTableModel snipers)
	{
		super("Auction Sniper");
		setName(APPLICATION_TITLE);
		this.snipers = snipers;
		fillContentPane(makeSnipersTable());
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void fillContentPane(JTable snipersTable)
	{
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
	}
	
	private JTable makeSnipersTable()
	{
		final JTable snipersTable = new JTable(snipers);
		snipersTable.setName(SNIPERS_TABLE_NAME);
		return snipersTable;
	}
}
