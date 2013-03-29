package auctionsniper.test.xmpp;

import static org.junit.Assert.*;

import java.util.logging.LogManager;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Test;

import auctionsniper.xmpp.LogWrapper;
import auctionsniper.xmpp.LoggingXMPPFailureReporter;

public class LoggingXMPPFailureReporterTest
{
	private final Mockery context = new Mockery();
	final LogWrapper logger = context.mock(LogWrapper.class);
	final LoggingXMPPFailureReporter failureReporter =
		new LoggingXMPPFailureReporter(logger);
	
	@After
	public void resetLogging()
	{
		LogManager.getLogManager().reset();
	}
	
	@Test
	public void writesMessageTranslationFailureToLog()
	{
		context.checking(new Expectations()
						{{
							oneOf(logger).severe("<auction id> " +
								"Could not translate message \"bad message\" " +
								"because \"java.lang.Exception: bad\"");
						}});
		
		failureReporter.cannotTranslateMessage(
			"auction id", "bad message", new Exception("bad"));
	}
}
