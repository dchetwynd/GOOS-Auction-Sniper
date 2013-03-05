package auctionsniper.test;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;

import auctionsniper.main.SniperState;

public class SniperStateTest
{

	@Test
	public void joiningStatusBecomesLostStatusWhenAuctionCloses() {
		SniperState initialStatus = SniperState.JOINING;
		assertThat(initialStatus.whenAuctionClosed(), equalTo(SniperState.LOST));
	}
	
	@Test
	public void biddingStatusBecomesLostStatusWhenAuctionCloses() {
		SniperState initialStatus = SniperState.BIDDING;
		assertThat(initialStatus.whenAuctionClosed(), equalTo(SniperState.LOST));
	}
	
	@Test
	public void winningStatusBecomesWonStatusWhenAuctionCloses() {
		SniperState initialStatus = SniperState.WINNING;
		assertThat(initialStatus.whenAuctionClosed(), equalTo(SniperState.WON));
	}

}
