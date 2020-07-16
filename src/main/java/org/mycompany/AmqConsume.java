package org.mycompany;

import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class AmqConsume {

	public String runExample() throws NamingException, JMSException {

		Connection connection = null;
		InitialContext initialContext = null;

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
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Create a JMS Message Consumer
			MessageConsumer messageConsumer = session.createConsumer(queue);

			// Start the Connection
			connection.start();

			// Receive the message
			TextMessage messageReceived = (TextMessage) messageConsumer.receive(5000);

			System.out.println("Received message: " + messageReceived.getText());

			return messageReceived.getText();
		} finally {
			// Be sure to close our JMS resources!
			if (initialContext != null) {
				initialContext.close();
			}
			if (connection != null) {
				connection.close();
			}
		}

	}
}
