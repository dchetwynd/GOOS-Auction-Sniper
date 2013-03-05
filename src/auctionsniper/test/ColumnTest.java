package auctionsniper.test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import auctionsniper.main.SniperSnapshot;
import auctionsniper.main.SniperState;
import auctionsniper.main.SnipersTableModel;
import auctionsniper.main.Column;

public class ColumnTest
{
	private final SniperSnapshot sniperSnapshot =
			new SniperSnapshot("item-12345", 100, 150, SniperState.BIDDING);
	
	
	@Test
	public void itemIdentifierRetrievedFromSniperSnapshot() {
		String itemIdentifier = (String)Column.ITEM_IDENTIFIER.valueIn(sniperSnapshot);
		assertThat(itemIdentifier, equalTo("item-12345"));
	}
	
	@Test
	public void lastPriceRetrievedFromSniperSnapshot() {
		Integer lastPrice = (Integer)Column.LAST_PRICE.valueIn(sniperSnapshot);
		assertThat(lastPrice, equalTo(100));
	}
	
	@Test
	public void lastBidRetrievedFromSniperSnapshot() {
		Integer lastBid = (Integer)Column.LAST_BID.valueIn(sniperSnapshot);
		assertThat(lastBid, equalTo(150));
	}
	
	@Test
	public void sniperStateRetrievedFromSniperSnapshot() {
		String sniperState = (String)Column.SNIPER_STATE.valueIn(sniperSnapshot);
		assertThat(sniperState, equalTo(SnipersTableModel.textFor(SniperState.BIDDING)));
	}

}
