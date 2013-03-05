package auctionsniper.main;

import java.util.HashMap;
import java.util.Map;

import auctionsniper.main.AuctionEventListener.PriceSource;

public class AuctionEvent {
	private final Map<String, String> fields = new HashMap<String, String>();
	
	public String type()
	{
		return get("Event");
	}
	
	public int currentPrice()
	{
		return getInt("CurrentPrice");
	}
	
	public int increment()
	{
		return getInt("Increment");
	}
	
	public PriceSource isFrom(String sniperId)
	{
		return sniperId.equals(bidder()) ? PriceSource.FromSniper : PriceSource.FromOtherBidder;
	}
	
	public static AuctionEvent from(String messageBody)
	{
		AuctionEvent event = new AuctionEvent();
		for (String field : fieldsIn(messageBody))
		{
			event.addField(field);
		}
		
		return event;
	}
	
	private String bidder()
	{
		return get("Bidder");
	}
	
	private int getInt(String fieldName)
	{
		return Integer.parseInt(get(fieldName));
	}
	
	private String get(String fieldName)
	{
		return fields.get(fieldName);
	}
	
	private void addField(String field)
	{
		String[] pair = field.split(":");
		fields.put(pair[0].trim(), pair[1].trim());
	}
	
	private static String[] fieldsIn(String messageBody)
	{
		return messageBody.split(";");
	}
}
