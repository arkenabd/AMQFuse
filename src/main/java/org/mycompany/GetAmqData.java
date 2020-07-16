package org.mycompany;

import java.util.Enumeration;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.jms.Connection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class GetAmqData {

	public String process(Exchange exchange) throws Exception {
		/*
		 * String url = (String) exchange.getProperty("fileRespPay");
		 * 
		 * ConsumerTemplate template = exchange.getContext().createConsumerTemplate();
		 * Exchange fileExchange = template.receive(url, 29000);
		 * exchange.getOut().setBody(fileExchange.getIn().getBody());
		 * System.out.println("Get message from callback :" +
		 * fileExchange.getIn().getBody()); template.doneUoW(fileExchange);
		 */

		Connection connection = null;
		InitialContext initialContext = null;
		Session session = null;
		try {
			// Step 1. Create an initial context to perform the JNDI lookup.
			Properties p = new Properties();
			p.put("java.naming.factory.initial", "org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory");
			p.put("connectionFactory.ConnectionFactory", "tcp://localhost:61616");
			p.put("queue.queue/exampleQueue", "exampleQueue");
			initialContext = new InitialContext(p);

			// lookup on the queue
			Queue queue = (Queue) initialContext.lookup("queue/exampleQueue");

			// lookup on the Connection Factory
			ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");

			// Create an authenticated JMS Connection
			connection = cf.createConnection("admin", "admin");

			// Create a JMS Session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			// Create a JMS Message Consumer
			MessageConsumer messageConsumer = session.createConsumer(queue,
					"JMSCorrelationID='" + exchange.getIn().getHeader("id") + "'");

			// Start the Connection
			connection.start();

			// Receive the message
			TextMessage messageReceived = (TextMessage) messageConsumer.receive(10000);

			System.out.println("Received message: " + messageReceived.getText());
			return messageReceived.getText().toString();
		} catch (Exception e) {
			System.out.println("Data not Found");
			return "Data not Found";
		} finally {
			// Be sure to close our JMS resources!
			if (session != null) {
				session.close();
			}
			if (initialContext != null) {
				initialContext.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	}
}