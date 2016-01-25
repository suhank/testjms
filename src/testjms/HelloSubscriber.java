package testjms;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.jms.Topic;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.Session;
import javax.jms.MessageListener;
import javax.jms.JMSException;

public class HelloSubscriber implements MessageListener {

	TopicConnection topicConnection;

	TopicSession topicSession;

	TopicSubscriber topicSubscriber;

	Topic topic;

	public HelloSubscriber(String factoryJNDI, String topicJNDI)

	throws JMSException, NamingException

	{
		Hashtable props = new Hashtable();

		props.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.jnp.interfaces.NamingContextFactory");

		props.put(Context.PROVIDER_URL, "localhost:1099");

		props.put("java.naming.rmi.security.manager", "yes");

		props.put(Context.URL_PKG_PREFIXES, "org.jboss.naming");

		Context context = new InitialContext(props);

		//Context context = new InitialContext();

		TopicConnectionFactory topicFactory =

		(TopicConnectionFactory) context.lookup(factoryJNDI);

		topicConnection = topicFactory.createTopicConnection();

		topicSession = topicConnection.createTopicSession(

		false, Session.AUTO_ACKNOWLEDGE);

		topic = (Topic) context.lookup(topicJNDI);

		topicSubscriber = topicSession.createSubscriber(topic);

		topicSubscriber.setMessageListener(this);

		System.out.println(

		"HelloSubscriber subscribed to topic: " + topicJNDI);

		topicConnection.start();

	}

	public void onMessage(Message m) {

		try {

			String msg = ((TextMessage) m).getText();

			System.out.println("HelloSubscriber got message: " + msg);

		} catch (JMSException ex) {

			System.err.println("Could not get text message: " + ex);

			ex.printStackTrace();

		}

	}

	public void close() throws JMSException {

		topicSession.close();

		topicConnection.close();

	}

	public static void main(String[] args) {

		try {

			HelloSubscriber subscriber = new HelloSubscriber(

			"TopicConnectionFactory",

			"topic/testTopic");

		} catch (Exception ex) {

			System.err.println(

			"An exception occurred while testing HelloSubscriber: " + ex);

			ex.printStackTrace();

		}

	}

}