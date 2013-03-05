package auctionsniper.main;

public class SwingThreadSniperListener implements SniperListener
{
	private SnipersTableModel uiTableModel;
	
	public SwingThreadSniperListener(SnipersTableModel ui)
	{
		this.uiTableModel = ui;
	}
	
	public void sniperStateChanged(SniperSnapshot sniperSnapshot)
	{
		uiTableModel.sniperStateChanged(sniperSnapshot);
	}
}
