package auctionsniper.test;

import static org.junit.Assert.*;

import javax.swing.event.*;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;

import auctionsniper.main.SniperSnapshot;
import auctionsniper.main.SniperState;
import auctionsniper.main.SnipersTableModel;
import auctionsniper.main.Column;
import static auctionsniper.main.SnipersTableModel.textFor;

public class SnipersTableModelTest
{
	private final Mockery context = new Mockery();
	private TableModelListener listener = context.mock(TableModelListener.class);
	private final SnipersTableModel model = new SnipersTableModel();
	
	@Before
	public void attachModelListener()
	{
		model.addTableModelListener(listener);
	}
	
	@Test
	public void hasEnoughColumns() {
		assertThat(model.getColumnCount(), equalTo(Column.values().length));
	}
	
	@Test
	public void setsUpColumnHeadings()
	{
		for (Column column: Column.values())
		{
			assertEquals(column.name, model.getColumnName(column.ordinal()));
		}
	}
	
	@Test
	public void setsSniperValuesInColumns()
	{
		context.checking(new Expectations()
						 {{
							 one(listener).tableChanged(with(aRowChangedEvent()));
						 }});
		
		model.sniperStateChanged(
				new SniperSnapshot("item id", 555, 666, SniperState.BIDDING));
		
		context.assertIsSatisfied();
		assertColumnEquals(Column.ITEM_IDENTIFIER, "item id");
		assertColumnEquals(Column.LAST_PRICE, 555);
		assertColumnEquals(Column.LAST_BID, 666);
		assertColumnEquals(Column.SNIPER_STATE, textFor(SniperState.BIDDING));
	}
	
	private void assertColumnEquals(Column column, Object expected)
	{
		final int rowIndex = 0;
		final int columnIndex = column.ordinal();
		assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
	}
	
	private Matcher<TableModelEvent> aRowChangedEvent()
	{
		return samePropertyValuesAs(new TableModelEvent(model, 0));
	}
}
