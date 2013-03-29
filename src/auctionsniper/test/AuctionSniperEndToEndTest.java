package auctionsniper.test;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

public class AuctionSniperEndToEndTest
{
	private final FakeAuctionServer auction1 = new FakeAuctionServer("item-54321");
	private final FakeAuctionServer auction2 = new FakeAuctionServer("item-65432");
	private final ApplicationRunner application = new ApplicationRunner();
	
	@Test
	public void sniperJoinsAuctionUntilAuctionCloses() throws Exception 
	{
		auction1.startSellingItem();
		application.startBiddingIn(auction1);
		auction1.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
		auction1.announceClosed();
		application.showsSniperHasLostAuction(auction1, 0, 0);
	}
	
	@Test
	public void sniperMakesAHigherBidButLoses() throws Exception
	{
		auction1.startSellingItem();
		
		application.startBiddingIn(auction1);
		auction1.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
		
		auction1.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(auction1, 1000, 1098);
		
		auction1.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction1.announceClosed();
		application.showsSniperHasLostAuction(auction1, 1000, 1098);
	}
	
	@Test
	public void sniperWinsAnAuctionByBiddingHigher() throws Exception
	{
		auction1.startSellingItem();
		
		application.startBiddingIn(auction1);
		auction1.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
		
		auction1.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(auction1, 1000, 1098);
		
		auction1.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction1.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		application.hasShownSniperIsWinning(auction1, 1098);
		
		auction1.announceClosed();
		application.showsSniperHasWonAuction(auction1, 1098);
	}
	
	@Ignore
	@Test
	public void sniperBidsForMultipleItems() throws Exception
	{
		auction1.startSellingItem();
		auction2.startSellingItem();
		
		application.startBiddingIn(auction1, auction2);
		auction1.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
		auction2.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
		
		auction1.reportPrice(1000, 98, "other bidder");
		auction1.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction2.reportPrice(500, 21, "other bidder");
		auction2.hasReceivedBid(521, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction1.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		auction2.reportPrice(521, 22, ApplicationRunner.SNIPER_XMPP_ID);
		
		application.hasShownSniperIsWinning(auction1, 1098);
		application.hasShownSniperIsWinning(auction2, 521);
		
		auction1.announceClosed();
		auction2.announceClosed();
		
		application.showsSniperHasWonAuction(auction1, 1098);
		application.showsSniperHasWonAuction(auction2, 521);
	}
	
	@Test
	public void sniperLosesAnAuctionWhenThePriceIsTooHigh() throws Exception
	{
		auction1.startSellingItem();
		application.startBiddingWithStopPrice(1100, auction1);
		auction1.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
		auction1.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(auction1, 1000, 1098);
		
		auction1.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction1.reportPrice(1197, 10, "third party");
		application.hasShownSniperIsLosing(auction1, 1197, 1098);
		
		auction1.reportPrice(1207, 10, "fourth party");
		application.hasShownSniperIsLosing(auction1, 1207, 1098);
		
		auction1.announceClosed();
		application.showsSniperHasLostAuction(auction1, 1207, 1098);
	}
	
	@Test
	public void sniperReportsInvalidAuctionMessageAndStopsRespondingToEvents() throws Exception
	{
		String brokenMessage = "A broken message";
		auction1.startSellingItem();
		auction2.startSellingItem();
		
		application.startBiddingIn(auction1, auction2);
		auction1.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
		
		auction1.reportPrice(500, 20, " other bidder");
		auction1.hasReceivedBid(520, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction1.sendInvalidMessageContaining(brokenMessage);
		application.showsSniperHasFailed(auction1);
		
		auction1.reportPrice(520, 21, "other bidder");
		waitForAnotherAuctionEvent();
		
		application.reportsInvalidMessage(auction1, brokenMessage);
		application.showsSniperHasFailed(auction1);
	}
	
	@After
	public void stopAuction()
	{
		auction1.stop();
	}

	@After
	public void stopApplication()
	{
		application.stop();
	}
	
	private void waitForAnotherAuctionEvent() throws Exception
	{
		auction2.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
		auction2.reportPrice(600, 6, "other bidder");
		application.hasShownSniperIsBidding(auction2, 600, 606);
	}
}
