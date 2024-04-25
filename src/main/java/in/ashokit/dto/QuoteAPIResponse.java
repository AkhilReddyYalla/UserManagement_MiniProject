package in.ashokit.dto;

import in.ashokit.entity.QuoteDto;

public class QuoteAPIResponse {
	
	private QuoteDto[] quotes;

	public QuoteDto[] getQuotes() {
		return quotes;
	}

	public void setQuotes(QuoteDto[] quotes) {
		this.quotes = quotes;
	}
	

}
