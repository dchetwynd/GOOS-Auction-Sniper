package auctionsniper.main;

import java.util.HashMap;
import java.util.Map;

import auctionsniper.main.AuctionEventListener.PriceSource;

public class AuctionEvent {
	private final Map<String, String> fields = new HashMap<String, String>();
	
	public String type() throws MissingValueException
	{
		return get("Event");
	}
	
	public int currentPrice() throws MissingValueException
	{
		return getInt("CurrentPrice");
	}
	
	public int increment() throws MissingValueException
	{
		return getInt("Increment");
	}
	
	public PriceSource isFrom(String sniperId) throws MissingValueException
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
	
	private String bidder() throws MissingValueException
	{
		return get("Bidder");
	}
	
	private int getInt(String fieldName) throws MissingValueException
	{
		return Integer.parseInt(get(fieldName));
	}
	
	private String get(String fieldName) throws MissingValueException
	{
		String value = fields.get(fieldName);
		
		if (value == null)
		{
			throw new MissingValueException(fieldName);
		}
		
		return value;
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
