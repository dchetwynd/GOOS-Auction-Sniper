package auctionsniper.main;

public interface Auction
{
	public void bid(int amount);
	public void join();
	public void addActionEventListener(AuctionEventListener listener);
}
