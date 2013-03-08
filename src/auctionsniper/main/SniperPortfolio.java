package auctionsniper.main;

public class SniperPortfolio implements SniperCollector
{
	private final Announcer<PortfolioListener> portfolioListeners =
			Announcer.to(PortfolioListener.class);
	
	public void addSniper(AuctionSniper newSniper)
	{
		portfolioListeners.announce().sniperAdded(newSniper);
	}
	
	public void addPortfolioListener(PortfolioListener listener)
	{
		portfolioListeners.addListener(listener);
	}
}
