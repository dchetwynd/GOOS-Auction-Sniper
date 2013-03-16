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
	private XMPPFailureReporter failureReporter;
	
	public AuctionMessageTranslator(String sniperId, AuctionEventListener listener,
		XMPPFailureReporter failureReporter)
	{
		this.sniperId = sniperId;
		this.listener = listener;
		this.failureReporter = failureReporter;
	}
	
	@Override
	public void processMessage(Chat chat, Message message)
	{
		String messageBody = message.getBody();
		try
		{
			translate(messageBody);
		} catch (Exception parseException) {
			failureReporter.cannotTranslateMessage(
				sniperId, messageBody, parseException);
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
