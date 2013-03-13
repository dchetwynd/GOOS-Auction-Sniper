package auctionsniper.main;

import java.io.File;

import org.hamcrest.Matcher;

public class AuctionLogDriver
{
	public static final String LOG_FILE_NAME = "auction-sniper.log";
	private final File logFile = new File(LOG_FILE_NAME);
	
	public void hasEntry(Matcher<String> entryMatcher)
	{
		//assertThat(FileUtils.readFileToString(logFile), entryMatcher);
	}
	
	public void clearLog()
	{
		
	}
}
