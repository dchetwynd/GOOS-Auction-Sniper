package auctionsniper.test;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Before;
import org.junit.Test;

import auctionsniper.main.Auction;
import auctionsniper.main.AuctionEventListener.PriceSource;
import auctionsniper.main.AuctionSniper;
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
	private AuctionSniper sniper;
	
	@Before
	public void addSniperListener()
	{
		sniper = new AuctionSniper(ITEM_ID, auction);
		sniper.addSniperListener(sniperListener);
	}
	
	@Test
	public void reportsLostIfAuctionClosesImmediately()
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
	public void reportsLostIfAuctionClosesWhenBidding()
	{
		final int currentPrice = 123;
		final int increment = 45;
		final int bid = 168;
		
		context.checking(new Expectations()
						 {{
							ignoring(auction);
							allowing(sniperListener).sniperStateChanged(
									new SniperSnapshot(ITEM_ID, 123, 168, SniperState.BIDDING));
								then(sniperState.is("bidding"));
								
							atLeast(1).of(sniperListener).sniperStateChanged(
									new SniperSnapshot(ITEM_ID, currentPrice, bid, SniperState.LOST));
								when(sniperState.is("bidding"));
						 }});
		
		sniper.currentPrice(currentPrice, increment, PriceSource.FromOtherBidder);
		sniper.auctionClosed();
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void reportsWonIfAuctionClosesWhenWinning()
	{
		final int currentPrice = 123;
		final int increment = 45;
		
		context.checking(new Expectations()
						 {{
							 ignoring(auction);
							 allowing(sniperListener).sniperStateChanged(
									 new SniperSnapshot(ITEM_ID, currentPrice, 0, SniperState.WINNING));
							 
							 atLeast(1).of(sniperListener).sniperStateChanged(
									 new SniperSnapshot(ITEM_ID, currentPrice, 0, SniperState.WON));
						 }});
		
		sniper.currentPrice(currentPrice, increment, PriceSource.FromSniper);
		sniper.auctionClosed();
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void bidsHigherAndReportsBiddingWhenNewPriceArrives()
	{
		final int price = 1001;
		final int increment = 25;
		final int bid = 1026;
		
		context.checking(new Expectations()
						 {{
							 ignoring(auction);
							 atLeast(1).of(sniperListener).sniperStateChanged(
									 new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
						 }});
		
		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void reportsIsWinningWhenCurrentPriceComesFromSniper()
	{
		context.checking(new Expectations()
						 {{
							ignoring(auction);
							allowing(sniperListener).sniperStateChanged(
									new SniperSnapshot(ITEM_ID, 123, 135, SniperState.BIDDING));
								then(sniperState.is("bidding"));
							
							atLeast(1).of(sniperListener).sniperStateChanged(
									new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING));
								when(sniperState.is("bidding"));
									
						 }});
		
		sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
		sniper.currentPrice(135, 45, PriceSource.FromSniper);
		
		context.assertIsSatisfied();
	}
}
