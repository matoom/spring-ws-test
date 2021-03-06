/**
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.javacrumbs.springws.test.helper;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.javacrumbs.springws.test.AbstractMessageTest;
import net.javacrumbs.springws.test.WsTestException;
import net.javacrumbs.springws.test.context.WsTestContext;
import net.javacrumbs.springws.test.context.WsTestContextHolder;
import net.javacrumbs.springws.test.util.DefaultXmlUtil;

import org.custommonkey.xmlunit.Diff;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.WebServiceMessageReceiver;
import org.w3c.dom.Document;

public class DefaultWsTestHelperTest extends AbstractMessageTest{

	private static final String CONTEXT_SERVER_DISPATCHER_PATH = "net/javacrumbs/springws/test/helper/dispatcher.xml";
	private DefaultWsTestHelper wsTestHelper;
	
	
	@Before
	public void setUp() throws Exception
	{
		wsTestHelper = new DefaultWsTestHelper();
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONTEXT_SERVER_DISPATCHER_PATH);
		wsTestHelper.setApplicationContext(applicationContext);
		wsTestHelper.afterPropertiesSet();
	}
	
	@Test
	public void testCreateDefault() throws Exception
	{
		DefaultWsTestHelper wsServerTestHelper = new DefaultWsTestHelper();
		wsServerTestHelper.afterPropertiesSet();
		assertTrue(wsServerTestHelper.getWebServiceTemplate() instanceof WsTestWebServiceTemplate);
		assertTrue(wsServerTestHelper.getMessageFactory() instanceof WebServiceMessageFactory);
		
		assertNotNull(((InMemoryWebServiceMessageSender)wsTestHelper.getWebServiceTemplate().getMessageSenders()[0]).getWebServiceMessageReceiver());
	}
	@Test
	public void testInitializeSet() throws Exception
	{
		DefaultWsTestHelper wsServerTestHelper = new DefaultWsTestHelper();
		wsServerTestHelper.afterPropertiesSet();
		
		WebServiceMessageFactory messageFactory = new SaajSoapMessageFactory();
		wsServerTestHelper.setMessageFactory(messageFactory);
		
		assertSame(messageFactory, wsServerTestHelper.getMessageFactory());
	}
	@Test
	public void testInitializeFromApplicationContext() throws Exception
	{
		DefaultWsTestHelper wsTestHelper = new DefaultWsTestHelper();
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONTEXT_SERVER_DISPATCHER_PATH);
		wsTestHelper.setApplicationContext(applicationContext);
		wsTestHelper.afterPropertiesSet();
		
		WebServiceMessageReceiver messageReceiver = (WebServiceMessageReceiver) applicationContext.getBean("messageReceiver");
		SoapMessageFactory messageFactory = (SoapMessageFactory) applicationContext.getBean("messageFactory");
		InMemoryWebServiceMessageSender messageSender = (InMemoryWebServiceMessageSender)wsTestHelper.getWebServiceTemplate().getMessageSenders()[0];
		assertEquals(messageReceiver,messageSender.getWebServiceMessageReceiver());
		assertEquals(messageFactory,messageSender.getMessageFactory());
		assertEquals(messageFactory,wsTestHelper.getMessageFactory());
	}
	
	@Test
	public void testSendMessageAndCompareResponse() throws Exception
	{
		MessageContext message = wsTestHelper.receiveMessage("xml/valid-message.xml");
		assertNotNull(message);
		assertNotNull(message.getResponse().getPayloadSource());
		
		wsTestHelper.createMessageValidator(message.getResponse()).compare("mock-responses/test/default-response.xml");
	}
	@Test
	public void testSendPayloadMessageAndCompareResponse() throws Exception
	{
		MessageContext message = wsTestHelper.receiveMessage("xml/valid-message-payload.xml");
		assertNotNull(message);
		assertNotNull(message.getResponse().getPayloadSource());
		
		wsTestHelper.createMessageValidator(message.getResponse()).compare("mock-responses/test/default-response.xml");
	}
	@Test
	public void testSendMessageAndCompareResponseFail() throws Exception
	{
		MessageContext message = wsTestHelper.receiveMessage("xml/valid-message.xml");
		assertNotNull(message);
		assertNotNull(message.getResponse().getPayloadSource());
		
		try
		{
			wsTestHelper.createMessageValidator(message.getResponse()).compare("mock-responses/test/different-response.xml");
			fail("Exception expected");
		}
		catch(WsTestException e)
		{
			//ok
		}
	}
	@Test
	public void testLoadMessage() throws Exception
	{
		WebServiceMessage message = wsTestHelper.loadMessage("xml/valid-message.xml");
		assertNotNull(message);
		Document document = DefaultXmlUtil.getInstance().loadDocument(message.getPayloadSource());
		assertNotNull(document);
	}
	@Test
	public void testLoadTemplate() throws Exception
	{
		WsTestContextHolder.getTestContext().setAttribute("a", 1);
		WsTestContextHolder.getTestContext().setAttribute("b", 2);

		WebServiceMessage message = wsTestHelper.loadMessage("xml/request-context-xslt.xml");
		Document controlDocument = loadDocument("xml/request1-envelope.xml");
		Diff diff = new Diff(controlDocument, loadDocument(message));
		assertTrue(diff.toString(), diff.similar());
	}
	
	@Test
	public void testLoadAttributesFromPropertiesFile() throws Exception{	

		WsTestContext wsContext = WsTestContextHolder.getTestContext();
		
		wsContext.setAttributes(PropertiesLoaderUtils.loadProperties(new ClassPathResource("properties/variables.properties")));
		String a= (String)wsContext.getAttribute("a");
		assertEquals(a, "1");
		
		String b= (String)wsContext.getAttribute("b");
		assertEquals(b, "2");
	}
	
	
	@Test
	public void testInterceptorOk() throws Exception
	{
		ClientInterceptor interceptor = createMock(ClientInterceptor.class);
		expect(interceptor.handleRequest((MessageContext)anyObject())).andReturn(true);
		expect(interceptor.handleResponse((MessageContext)anyObject())).andReturn(true);
		
		replay(interceptor);

		DefaultWsTestHelper wsTestHelper = new DefaultWsTestHelper();
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONTEXT_SERVER_DISPATCHER_PATH);
		wsTestHelper.setInterceptors(new ClientInterceptor[]{interceptor});
		wsTestHelper.setApplicationContext(applicationContext);
		wsTestHelper.afterPropertiesSet();
		
		wsTestHelper.receiveMessage("xml/valid-message.xml");
		
		verify(interceptor);
				
	}
	
	@Test
	public void testReceiveError() throws Exception
	{
		MessageContext context = wsTestHelper.receiveMessage("xml/invalid-message.xml");
		assertNotNull(context);		
	}
	
}
