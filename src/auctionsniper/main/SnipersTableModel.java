package auctionsniper.main;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.objogate.exception.Defect;

public class SnipersTableModel extends AbstractTableModel
	implements SniperListener, PortfolioListener
{
	private static String[] STATUS_TEXT =
		{
			"Joining Auction",
			"Bidding in Auction",
			"Winning in Auction",
			"Losing in Auction",
			"Lost Auction",
			"Won Auction"
		};
	
	private List<SniperSnapshot> snipers = new ArrayList<SniperSnapshot>();
	
	public int getColumnCount()
	{
		return Column.values().length;
	}
	
	public int getRowCount()
	{
		return snipers.size();
	}
	
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return Column.at(columnIndex).valueIn(snipers.get(rowIndex));
	}
	
	@Override
	public String getColumnName(int columnIndex)
	{
		return Column.at(columnIndex).name;
	}
	
	public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
		int rowIndex = rowMatching(newSniperSnapshot);
		snipers.set(rowIndex, newSniperSnapshot);
		fireTableRowsUpdated(rowIndex, rowIndex);
	}
	
	private int rowMatching(SniperSnapshot snapshot)
	{
		for (int i = 0; i < snipers.size(); i++)
		{
			if (snapshot.isForSameItemAs(snipers.get(i)))
			{
				return i;
			}
		}
		
		throw new Defect("Cannot find match for " + snapshot);
	}
	
	public static String textFor(SniperState state)
	{
		return STATUS_TEXT[state.ordinal()];
	}
	
	public void sniperAdded(AuctionSniper newSniper)
	{
		addSniperSnapshot(newSniper.getSnapshot());
		newSniper.addSniperListener(new SwingThreadSniperListener(this));
	}
	
	private void addSniperSnapshot(SniperSnapshot newSniper)
	{
		snipers.add(newSniper);
		int lastRowIndex = snipers.size() - 1;
		fireTableRowsInserted(lastRowIndex, lastRowIndex);
	}
}