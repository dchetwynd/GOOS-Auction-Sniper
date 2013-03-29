package auctionsniper.test.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import auctionsniper.main.AuctionEventListener;
import auctionsniper.main.AuctionEventListener.PriceSource;
import auctionsniper.test.ApplicationRunner;
import auctionsniper.xmpp.AuctionMessageTranslator;
import auctionsniper.xmpp.XMPPFailureReporter;

public class AuctionMessageTranslatorTest
{
	public static final Chat UNUSED_CHAT = null;
	
	private final Mockery context = new Mockery();
	private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
	private final XMPPFailureReporter failureReporter = context.mock(XMPPFailureReporter.class);
	private final AuctionMessageTranslator translator =
		new AuctionMessageTranslator(ApplicationRunner.SNIPER_ID, listener, failureReporter);
	
	@Test
	public void notifiesAuctionClosedWhenCloseMessageReceived()
	{
		context.checking(new Expectations()
						{{
							oneOf(listener).auctionClosed();
						}});
		Message message = new Message();
		message.setBody("SOLVersion: 1.1; Event: CLOSE;");
		
		translator.processMessage(UNUSED_CHAT, message);
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder()
	{
		context.checking(new Expectations()
						{{
							exactly(1).of(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
						}});
		
		Message message = new Message();
		message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");
		
		translator.processMessage(UNUSED_CHAT, message);
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper()
	{
		context.checking(new Expectations()
						{{
							exactly(1).of(listener).currentPrice(234, 5, PriceSource.FromSniper);
						}});
		
		Message message = new Message();
		message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; " +
				"Bidder: " + ApplicationRunner.SNIPER_ID + ";");
		
		translator.processMessage(UNUSED_CHAT, message);
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void notifiesAuctionFailedWhenBadMessageReceived()
	{
		String badMessage = "a bad message";
		expectFailureWithMessage(badMessage);
		
		translator.processMessage(UNUSED_CHAT, message(badMessage));
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void notifiesAuctionFailedWhenEventTypeMissing()
	{
		String badMessage = "SOLVersion: 1.1; CurrentPrice: 234; Increment: 5; Bidder: " +
			    ApplicationRunner.SNIPER_ID + ";";
		expectFailureWithMessage(badMessage);
		
		translator.processMessage(UNUSED_CHAT, message(badMessage));
		
		context.assertIsSatisfied();
	}
	
	private Message message(String messageBody)
	{
		Message message = new Message();
		message.setBody(messageBody);
		return message;
	}
	
	private void expectFailureWithMessage(final String badMessage)
	{
		context.checking(new Expectations()
						{{
							oneOf(listener).auctionFailed();
							oneOf(failureReporter).cannotTranslateMessage(
								with(ApplicationRunner.SNIPER_ID),
								with(badMessage),
								with(any(Exception.class)));
						}});
	}
}