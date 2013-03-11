package auctionsniper.test;

import static org.junit.Assert.*;

import javax.swing.event.*;
import static javax.swing.event.TableModelEvent.*;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.objogate.exception.Defect;

import static org.hamcrest.CoreMatchers.equalTo;

import auctionsniper.main.AuctionSniper;
import auctionsniper.main.Item;
import auctionsniper.main.SniperSnapshot;
import auctionsniper.main.SniperState;
import auctionsniper.main.SnipersTableModel;
import auctionsniper.main.Column;
import static auctionsniper.main.SnipersTableModel.textFor;

public class SnipersTableModelTest
{
	private final String ITEM_ID = "item-54321";
	private final Mockery context = new Mockery();
	private TableModelListener listener = context.mock(TableModelListener.class);
	private final SnipersTableModel model = new SnipersTableModel();
	private final AuctionSniper sniper1 = new AuctionSniper(
		new Item(ITEM_ID, Integer.MAX_VALUE), null);
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
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
	public void acceptsNewSniper()
	{
		context.checking(new Expectations()
						{{
							one(listener).tableChanged(with(anInsertionAtRow(0)));
						}});
		
		model.sniperAdded(sniper1);
		
		context.assertIsSatisfied();
		assertRowMatchesSnapshot(0, SniperSnapshot.joining(ITEM_ID));
	}
	
	@Test
	public void setsSniperValuesInColumns()
	{
		SniperSnapshot joining = SniperSnapshot.joining(ITEM_ID);
		SniperSnapshot bidding = joining.bidding(555, 666);
		context.checking(new Expectations()
						 {{
							 allowing(listener).tableChanged(with(anyInsertionEvent()));
							 one(listener).tableChanged(with(aChangeInRow(0)));
						 }});
		
		model.sniperAdded(sniper1);
		model.sniperStateChanged(bidding);
		
		context.assertIsSatisfied();
		assertRowMatchesSnapshot(0, bidding);
	}

	@Test
	public void holdsSnipersInAdditionOrder()
	{
		context.checking(new Expectations()
						 {{
							 ignoring(listener);
						 }});
		
		model.sniperAdded(sniper1);
		AuctionSniper sniper2 = new AuctionSniper(
			new Item("item-65432", Integer.MAX_VALUE), null);
		model.sniperAdded(sniper2);
		
		assertEquals(ITEM_ID, cellValue(0, Column.ITEM_IDENTIFIER));
		assertEquals("item-65432", cellValue(1, Column.ITEM_IDENTIFIER));
	}
	
	@Test
	public void notifiesListenersWhenAddingASniper()
	{
		SniperSnapshot joining = SniperSnapshot.joining(ITEM_ID);
		context.checking(new Expectations()
						 {{
							one(listener).tableChanged(with(anInsertionAtRow(0)));
						 }});
		
		assertEquals(0, model.getRowCount());
		
		model.sniperAdded(sniper1);
		
		context.assertIsSatisfied();
		assertEquals(1, model.getRowCount());
		assertRowMatchesSnapshot(0, joining);
	}
	
	@Test
	public void updatesCorrectRowForSniper()
	{
		AuctionSniper sniper2 = new AuctionSniper(
			new Item("item-65432", Integer.MAX_VALUE), null);
		context.checking(new Expectations()
						 {{
							 allowing(listener).tableChanged(with(anyInsertionEvent()));
							 one(listener).tableChanged(with(aChangeInRow(1)));
						 }});
		
		model.sniperAdded(sniper1);
		model.sniperAdded(sniper2);
		SniperSnapshot winning1 = sniper2.getSnapshot().winning(123);
		model.sniperStateChanged(winning1);
		
		context.assertIsSatisfied();
		assertRowMatchesSnapshot(1, winning1);
	}
	
	@Test(expected=Defect.class)
	public void throwsDefectIfNoExistingSniperForAnUpdate()
	{
		model.sniperStateChanged(new SniperSnapshot("item 1", 123, 234, SniperState.WINNING));
	}
	
	private Matcher<TableModelEvent> anInsertionAtRow(int rowIndex)
	{
		return samePropertyValuesAs(new TableModelEvent(model, rowIndex, rowIndex, ALL_COLUMNS, INSERT));
	}
	
	private Matcher<TableModelEvent> anyInsertionEvent()
	{
		Matcher<Integer> insertMatcher = equalTo(TableModelEvent.INSERT);
		return new FeatureMatcher<TableModelEvent, Integer>(insertMatcher, "is an insertion event", "event type")
		{
			@Override
			protected Integer featureValueOf(TableModelEvent actual)
			{
				return actual.getType();
			}
		};
	}
	
	private Matcher<TableModelEvent> aChangeInRow(int rowIndex)
	{
		return samePropertyValuesAs(new TableModelEvent(model, rowIndex));
	}
	
	private void assertRowMatchesSnapshot(int rowIndex, SniperSnapshot snapshot)
	{
		assertColumnEquals(rowIndex, Column.ITEM_IDENTIFIER, snapshot.itemId);
		assertColumnEquals(rowIndex, Column.LAST_PRICE, snapshot.lastPrice);
		assertColumnEquals(rowIndex, Column.LAST_BID, snapshot.lastBid);
		assertColumnEquals(rowIndex, Column.SNIPER_STATE, textFor(snapshot.state));
	}
	
	private void assertColumnEquals(int rowIndex, Column column, Object expected)
	{
		final int columnIndex = column.ordinal();
		assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
	}
	
	private Object cellValue(int rowIndex, Column column)
	{
		return model.getValueAt(rowIndex, column.ordinal());
	}
}
