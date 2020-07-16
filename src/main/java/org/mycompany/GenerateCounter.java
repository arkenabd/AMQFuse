package org.mycompany;

import org.apache.camel.Exchange;

public class GenerateCounter {
	public int counter = 0;

	public void getCounter(String input, Exchange exchange) {
		counter = counter + 1;
		exchange.setProperty("counter", String.valueOf(counter));

	}

}
