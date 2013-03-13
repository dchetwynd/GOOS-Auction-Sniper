package auctionsniper.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.States;
import org.junit.Before;
import org.junit.Test;

import auctionsniper.main.Auction;
import auctionsniper.main.AuctionEventListener.PriceSource;
import auctionsniper.main.AuctionSniper;
import auctionsniper.main.Item;
import auctionsniper.main.SniperListener;
import auctionsniper.main.SniperSnapshot;
import auctionsniper.main.SniperState;

public class AuctionSniperTest 
{
	private final Mockery context = new Mockery();
	private final SniperListener sniperListener = context.mock(SniperListener.class);
	private final Auction auction = context.mock(Auction.class);
	private final States sniperState = context.states("sniper");
	
	private final String ITEM_ID = "item-54321";
	public final Item ITEM = new Item(ITEM_ID, 1234);
	private final AuctionSniper sniper = new AuctionSniper(ITEM, auction);
	
	@Before
	public void addSniperListener()
	{
		sniper.addSniperListener(sniperListener);
	}
	
	@Test
	public void hasInitialStateOfJoining()
	{
		assertThat(sniper.getSnapshot(), samePropertyValuesAs(SniperSnapshot.joining(ITEM_ID)));
	}
	
	@Test
	public void reportsLostWhenAuctionClosesImmediately()
	{
		context.checking(new Expectations()
						 {{
							atLeast(1).of(sniperListener).sniperStateChanged(
									new SniperSnapshot(ITEM_ID, 0, 0, SniperState.LOST));
						 }});
		
		sniper.auctionClosed();
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void bidsHigherAndReportsBiddingWhenNewPriceArrives()
	{
		final int price = 1001;
		final int increment = 25;
		final int bid = price + increment;
		
		context.checking(new Expectations()
						 {{
							 one(auction).bid(bid);
							 
							 atLeast(1).of(sniperListener).sniperStateChanged(
									 new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
						 }});
		
		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice()
	{
		final int price = 1233;
		final int increment = 25;
		
		context.checking(new Expectations()
						{{
							atLeast(1).of(sniperListener).sniperStateChanged(
								new SniperSnapshot(ITEM_ID, price, 0, SniperState.LOSING));
						}});
		
		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice()
	{
		allowingSniperBidding();
		context.checking(new Expectations()
						{{
							int bid = 123 + 45;
							allowing(auction).bid(bid);
							atLeast(1).of(sniperListener).sniperStateChanged(
								new SniperSnapshot(ITEM_ID, 2345, bid, SniperState.LOSING));
								when(sniperState.is("bidding"));
						}});
		
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice()
	{
		final int price = 1233;
		final int increment = 25;
		
		allowingSniperBidding();
		allowingSniperWinning();
		
		context.checking(new Expectations()
						{{
							int bid = 123 + 45;
							allowing(auction).bid(bid);
							
							atLeast(1).of(sniperListener).sniperStateChanged(
								new SniperSnapshot(ITEM_ID, price, bid, SniperState.LOSING));
								when(sniperState.is("winning"));
						}});
		
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.currentPrice(168, 45, PriceSource.FromSniper);
		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void continuesToBeLosingOnceStopPriceHasBeenReached()
	{
		final Sequence states = context.sequence("sniper states");
		final int price1 = 1233;
		final int price2 = 1258;
		
		context.checking(new Expectations()
		 {{
			 atLeast(1).of(sniperListener).sniperStateChanged(
				new SniperSnapshot(ITEM_ID, price1, 0, SniperState.LOSING));
			 	inSequence(states);
			 
			 atLeast(1).of(sniperListener).sniperStateChanged(
				new SniperSnapshot(ITEM_ID, price2, 0, SniperState.LOSING));
			 	inSequence(states);
		 }});

		sniper.currentPrice(price1, 25, PriceSource.FromOtherBidder);
		sniper.currentPrice(price2, 25, PriceSource.FromOtherBidder);
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void reportsLostIfAuctionClosesWhenBidding()
	{
		allowingSniperBidding();
		ignoringAuction();
		
		context.checking(new Expectations()
						 {{
							atLeast(1).of(sniperListener).sniperStateChanged(
								new SniperSnapshot(ITEM_ID, 123, 168, SniperState.LOST));
								when(sniperState.is("bidding"));
						 }});
		
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.auctionClosed();
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void reportsLostIfAuctionClosesWhenLosing()
	{
		allowingSniperLosing();
		context.checking(new Expectations()
						 {{
							 atLeast(1).of(sniperListener).sniperStateChanged(
								new SniperSnapshot(ITEM_ID, 1230, 0, SniperState.LOST));
							 	when(sniperState.is("losing"));
						 }});
		
		sniper.currentPrice(1230, 456, PriceSource.FromOtherBidder);
		sniper.auctionClosed();
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void reportsIsWinningWhenCurrentPriceComesFromSniper()
	{
		allowingSniperBidding();
		ignoringAuction();
		
		context.checking(new Expectations()
						 {{
							atLeast(1).of(sniperListener).sniperStateChanged(
								new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING));
								when(sniperState.is("bidding"));
									
						 }});
		
		sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
		sniper.currentPrice(135, 45, PriceSource.FromSniper);
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void reportsWonIfAuctionClosesWhenWinning()
	{
		allowingSniperBidding();
		allowingSniperWinning();
		ignoringAuction();
		
		context.checking(new Expectations()
						 {{
							 atLeast(1).of(sniperListener).sniperStateChanged(
								new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WON));
							 	when(sniperState.is("winning"));
						 }});
		
		sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
		sniper.currentPrice(135, 45, PriceSource.FromSniper);
		sniper.auctionClosed();
		
		context.assertIsSatisfied();
	}

	@Test
	public void reportsFailedIfAuctionFailsWhenBidding()
	{
		ignoringAuction();
		allowingSniperBidding();
		
		expectSniperToFailWhenItIs("bidding");
		
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.auctionFailed();
		
		context.assertIsSatisfied();
	}
	
	private void expectSniperToFailWhenItIs(String state)
	{
		context.checking(new Expectations()
						 {{
							 atLeast(1).of(sniperListener).sniperStateChanged(
								new SniperSnapshot(ITEM_ID, 0, 0, SniperState.FAILED));
							 	when(sniperState.is("bidding"));
						 }});
	}
	
	private void allowingSniperBidding()
	{
		context.checking(new Expectations()
						 {{
							allowing(sniperListener).sniperStateChanged(
								with(aSniperThatIs(SniperState.BIDDING)));
								then(sniperState.is("bidding"));
						 }});
	}
	
	private void allowingSniperWinning()
	{
		context.checking(new Expectations()
						 {{
							allowing(sniperListener).sniperStateChanged(
								with(aSniperThatIs(SniperState.WINNING)));
								then(sniperState.is("winning"));
						 }});
	}
	
	private void allowingSniperLosing()
	{
		context.checking(new Expectations()
						 {{
							allowing(sniperListener).sniperStateChanged(
								with(aSniperThatIs(SniperState.LOSING)));
								then(sniperState.is("losing"));
						 }});
	}
	
	private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
		return new FeatureMatcher<SniperSnapshot, SniperState>(equalTo(state), "sniper that is ", "was")
		{
			@Override
			protected SniperState featureValueOf(SniperSnapshot actual)
			{
				return actual.state;
			}
		};
	}
	
	private void ignoringAuction()
	{
		context.checking(new Expectations() {{ ignoring(auction); }});
	}
}
