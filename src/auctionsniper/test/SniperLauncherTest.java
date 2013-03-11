package auctionsniper.test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.equalTo;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Test;

import auctionsniper.main.Auction;
import auctionsniper.main.AuctionEventListener;
import auctionsniper.main.AuctionHouse;
import auctionsniper.main.AuctionSniper;
import auctionsniper.main.Item;
import auctionsniper.main.SniperCollector;
import auctionsniper.main.SniperLauncher;

public class SniperLauncherTest
{
	private Mockery context = new Mockery();
	private final States auctionState = context.states("auction state")
											  .startsAs("not joined");
	private final Auction auction = context.mock(Auction.class);
	private final AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
	private final SniperCollector sniperCollector = context.mock(SniperCollector.class);
	private final SniperLauncher launcher = new SniperLauncher(auctionHouse, sniperCollector);
	
	@Test
	public void addsNewSniperToCollectorAndThenJoinsAuction()
	{
		final String itemId = "item-54321";
		final Item item = new Item(itemId, Integer.MAX_VALUE);
		context.checking(new Expectations()
							 {{
								allowing(auctionHouse).auctionFor(item);
									will(returnValue(auction));
								oneOf(auction).addAuctionEventListener(with(sniperForItem(itemId)));
									when(auctionState.is("not joined"));
								oneOf(sniperCollector).addSniper(with(sniperForItem(itemId)));
									when(auctionState.is("not joined"));
								one(auction).join();
									then(auctionState.is("joined"));
							 }});
		
		launcher.joinAuction(item);
	}
	
	private Matcher<AuctionSniper> sniperForItem(String itemId)
	{
		return new FeatureMatcher<AuctionSniper, String>(equalTo(itemId), "sniper with item id", "item id")
		{
			@Override
			protected String featureValueOf(AuctionSniper actual)
			{
				return actual.getSnapshot().itemId;
			}
		};
	}

}
