package auctionsniper.main;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

public class AuctionMessageTranslator implements MessageListener
{
	private String sniperId;
	private AuctionEventListener listener;
	
	public AuctionMessageTranslator(String sniperId, AuctionEventListener listener)
	{
		this.sniperId = sniperId;
		this.listener = listener;
	}
	
	@Override
	public void processMessage(Chat chat, Message message)
	{
		AuctionEvent event = AuctionEvent.from(message.getBody());
		String eventType = event.type();
		
		if (eventType.equals("CLOSE"))
		{
			listener.auctionClosed();
		}
		else if (eventType.equals("PRICE"))
		{
			listener.currentPrice(event.currentPrice(),
								 event.increment(),
								 event.isFrom(sniperId));
		}
	}
}
