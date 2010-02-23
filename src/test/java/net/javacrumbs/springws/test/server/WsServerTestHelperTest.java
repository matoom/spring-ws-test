package net.javacrumbs.springws.test.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.MessageDispatcher;
import org.springframework.ws.transport.WebServiceMessageReceiver;

public class WsServerTestHelperTest {

	@Test
	public void testGetWebServiceMessageReceiver() throws Exception
	{
		WsServerTestHelper wsServerTestHelper = new WsServerTestHelper();
		wsServerTestHelper.afterPropertiesSet();
		assertTrue(wsServerTestHelper.getWebServiceMessageReceiver() instanceof WebServiceMessageReceiver);
	}
	@Test
	public void testGetWebServiceMessageReceiverSet() throws Exception
	{
		WsServerTestHelper wsServerTestHelper = new WsServerTestHelper();
		MessageDispatcher dispatcher = new MessageDispatcher();
		wsServerTestHelper.setWebServiceMessageReceiver(dispatcher);
		wsServerTestHelper.afterPropertiesSet();
		assertSame(dispatcher, wsServerTestHelper.getWebServiceMessageReceiver());
	}
	@Test
	public void testGetWebServiceMessageReceiverFromApplicationContext() throws Exception
	{
		
		WsServerTestHelper wsServerTestHelper = new WsServerTestHelper();
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("context/server/dispatcher.xml");
		wsServerTestHelper.setApplicationContext(applicationContext);
		wsServerTestHelper.afterPropertiesSet();
		
		assertTrue(wsServerTestHelper.getWebServiceMessageReceiver() instanceof WebServiceMessageReceiver);
		assertEquals(applicationContext.getBean("messageDispatcher"),wsServerTestHelper.getWebServiceMessageReceiver());
	}
	@Test(expected=NoSuchBeanDefinitionException.class)
	public void testGetWebServiceMessageReceiverFromApplicationContextTwoDispatchers() throws Exception
	{
		
		WsServerTestHelper wsServerTestHelper = new WsServerTestHelper();
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("context/server/two-dispatchers.xml");
		wsServerTestHelper.setApplicationContext(applicationContext);
		wsServerTestHelper.afterPropertiesSet();
		wsServerTestHelper.getWebServiceMessageReceiver();
	}
	
	@Test
	public void testSendMessage() throws Exception
	{
		WsServerTestHelper wsServerTestHelper = new WsServerTestHelper();
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("context/server/dispatcher.xml");
		wsServerTestHelper.setApplicationContext(applicationContext);
		wsServerTestHelper.afterPropertiesSet();
		MessageContext message = wsServerTestHelper.receiveMessage(new ClassPathResource("xml/valid-message.xml"));
		assertNotNull(message);
		assertNotNull(message.getResponse());
	}
}
