package auctionsniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import auctionsniper.main.AuctionEvent;
import auctionsniper.main.AuctionEventListener;
import auctionsniper.main.MissingValueException;

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
		try
		{
			translate(message.getBody());
		} catch (Exception parseException) {
			listener.auctionFailed();
		}
	}

	private void translate(String messageBody) throws MissingValueException {
		AuctionEvent event = AuctionEvent.from(messageBody);
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
