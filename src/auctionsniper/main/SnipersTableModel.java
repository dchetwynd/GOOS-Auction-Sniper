package auctionsniper.main;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel implements SniperListener
{
	private final static SniperSnapshot STARTING_UP =
			new SniperSnapshot("item-54321", 0, 0, SniperState.JOINING);
	private static String[] STATUS_TEXT =
		{
			"Joining Auction",
			"Bidding in Auction",
			"Winning in Auction",
			"Lost Auction",
			"Won Auction"};
	
	private SniperSnapshot sniperSnapshot = STARTING_UP;
	
	public int getColumnCount()
	{
		return Column.values().length;
	}
	
	public int getRowCount()
	{
		return 1;
	}
	
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return Column.at(columnIndex).valueIn(sniperSnapshot);
	}
	
	@Override
	public String getColumnName(int columnIndex)
	{
		return Column.at(columnIndex).name;
	}
	
	public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
		sniperSnapshot = newSniperSnapshot;
		fireTableRowsUpdated(0, 0);
	}
	
	public static String textFor(SniperState state)
	{
		return STATUS_TEXT[state.ordinal()];
	}
}