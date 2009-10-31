/**
 * Copyright 2006 the original author or authors.
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
package net.javacrumbs.springws.test.simple;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import net.javacrumbs.springws.test.AbstractMessageTest;
import net.javacrumbs.springws.test.MockWebServiceMessageSender;
import net.javacrumbs.springws.test.RequestProcessor;
import net.javacrumbs.springws.test.WsTestException;
import net.javacrumbs.springws.test.generator.DefaultResponseGenerator;
import net.javacrumbs.springws.test.lookup.ExpressionBasedResourceLookup;
import net.javacrumbs.springws.test.validator.XmlCompareRequestValidator;

import org.junit.Test;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.xml.transform.StringResult;


public class WsMockControlTest extends AbstractMessageTest{
	
	private static final String URI = "http://example.org";
	private static final Map<String, String> NS_MAP = Collections.singletonMap("ns", "http://www.example.org/schema");

	@Test
	public void testExpectAndReturn() throws IOException
	{
		MockWebServiceMessageSender sender = (MockWebServiceMessageSender)new WsMockControl().expectRequest("xml/control-message-test.xml").returnResponse("mock-responses/test/default-response.xml").createMock();
		assertNotNull(sender);
		assertEquals(2, sender.getRequestProcessors().size());
		
		ExpressionBasedResourceLookup lookup1 = (ExpressionBasedResourceLookup)((XmlCompareRequestValidator)extractRequestProcessor(sender,0)).getControlResourceLookup();
		assertEquals("xml/control-message-test.xml", lookup1.getResourceExpressions()[0]);
		
		ExpressionBasedResourceLookup lookup2 = (ExpressionBasedResourceLookup)((DefaultResponseGenerator)extractRequestProcessor(sender,1)).getResourceLookup();
		assertEquals("mock-responses/test/default-response.xml", lookup2.getResourceExpressions()[0]);
		
		WebServiceTemplate template = new WebServiceTemplate();
		template.setMessageSender(sender);
		StringResult responseResult = new StringResult();
		template.sendSourceAndReceiveToResult(URI,createMessage("xml/valid-message.xml").getPayloadSource(), responseResult );
	}
	
	@Test
	public void testWrapSimple()
	{
		RequestProcessor processor = createMock(RequestProcessor.class);
		replay(processor);
		
		WsMockControl control = new WsMockControl();
		control.addRequestProcessor(processor);
		LimitingRequestProcessorWrapper processorWrapper = (LimitingRequestProcessorWrapper)control.getRequestProcessors().get(0);
		assertEquals(processor.toString(),processorWrapper.getRequestProcessorDescription());
		assertEquals(1, processorWrapper.getMinNumberOfProcessedRequests());
		assertEquals(1, processorWrapper.getMaxNumberOfProcessedRequests());

		verify(processor);
		
	}
	@Test(expected=IllegalStateException.class)
	public void testTimesNoProcessor()
	{
		new WsMockControl().times(0, 1);
	}
	
	@Test
	public void testWrapTimes()
	{
		WsMockControl control = new WsMockControl();
		control.expectRequest("xml/does-not-exist.xml").times(0,5);
		LimitingRequestProcessorWrapper processorWrapper = (LimitingRequestProcessorWrapper)control.getRequestProcessors().get(0);
		assertEquals(0, processorWrapper.getMinNumberOfProcessedRequests());
		assertEquals(5, processorWrapper.getMaxNumberOfProcessedRequests());
		assertEquals("expectRequest(\"xml/does-not-exist.xml\")",processorWrapper.getRequestProcessorDescription());
	}
	@Test
	public void testWrapAtLeastOnce()
	{
		WsMockControl control = new WsMockControl();
		control.failIf("//ns:number!=1",NS_MAP).atLeastOnce();
		LimitingRequestProcessorWrapper processorWrapper = (LimitingRequestProcessorWrapper)control.getRequestProcessors().get(0);
		assertEquals(1, processorWrapper.getMinNumberOfProcessedRequests());
		assertEquals(Integer.MAX_VALUE, processorWrapper.getMaxNumberOfProcessedRequests());
		assertEquals("failIf(\"//ns:number!=1\")",processorWrapper.getRequestProcessorDescription());
	}
	@Test
	public void testWrapAnyTimes()
	{
		WsMockControl control = new WsMockControl();
		control.assertThat("//ns:number=1",NS_MAP).anyTimes();
		LimitingRequestProcessorWrapper processorWrapper = (LimitingRequestProcessorWrapper)control.getRequestProcessors().get(0);
		assertEquals(0, processorWrapper.getMinNumberOfProcessedRequests());
		assertEquals(Integer.MAX_VALUE, processorWrapper.getMaxNumberOfProcessedRequests());
		assertEquals("assertThat(\"//ns:number=1\")",processorWrapper.getRequestProcessorDescription());
	}
	@Test
	public void testWrapTimes1()
	{
		WsMockControl control = new WsMockControl();
		control.returnResponse("mock-responses/test/default-response.xml").times(5);
		LimitingRequestProcessorWrapper processorWrapper = (LimitingRequestProcessorWrapper)control.getRequestProcessors().get(0);
		assertEquals(5, processorWrapper.getMinNumberOfProcessedRequests());
		assertEquals(5, processorWrapper.getMaxNumberOfProcessedRequests());
		assertEquals("returnResponse(\"mock-responses/test/default-response.xml\")",processorWrapper.getRequestProcessorDescription());
	}
	@Test
	public void testWrapOnce()
	{
		WsMockControl control = new WsMockControl();
		control.throwException(new WsTestException("Test error")).once();
		LimitingRequestProcessorWrapper processorWrapper = (LimitingRequestProcessorWrapper)control.getRequestProcessors().get(0);
		assertEquals(1, processorWrapper.getMinNumberOfProcessedRequests());
		assertEquals(1, processorWrapper.getMaxNumberOfProcessedRequests());
		assertEquals("throwException(\"Test error\")",processorWrapper.getRequestProcessorDescription());
	}
	
	private RequestProcessor extractRequestProcessor(MockWebServiceMessageSender sender, int index) {
		return ((LimitingRequestProcessorWrapper)sender.getRequestProcessors().get(index)).getWrappedRequestProcessor();
	}
	
	@Test(expected=WsTestException.class)
	public void testExpectResourceNotFound() throws IOException
	{
		MockWebServiceMessageSender sender = (MockWebServiceMessageSender)new WsMockControl().expectRequest("xml/does-not-exist.xml").returnResponse("mock-responses/test/default-response.xml").createMock();
		assertNotNull(sender);
		assertEquals(2, sender.getRequestProcessors().size());
		
		
		WebServiceTemplate template = new WebServiceTemplate();
		template.setMessageSender(sender);
		StringResult responseResult = new StringResult();
		template.sendSourceAndReceiveToResult(URI,createMessage("xml/valid-message.xml").getPayloadSource(), responseResult );
	}
	@Test(expected=WsTestException.class)
	public void testVerifyUri() throws IOException, URISyntaxException
	{
		MockWebServiceMessageSender sender = (MockWebServiceMessageSender)new WsMockControl().expectUri(new URI("http://example.com")).returnResponse("mock-responses/test/default-response.xml").createMock();
		assertNotNull(sender);
		assertEquals(2, sender.getRequestProcessors().size());
		
		
		WebServiceTemplate template = new WebServiceTemplate();
		template.setMessageSender(sender);
		StringResult responseResult = new StringResult();
		template.sendSourceAndReceiveToResult(URI,createMessage("xml/valid-message.xml").getPayloadSource(), responseResult );
	}
	@Test(expected=WsTestException.class)
	public void testXPathValidation() throws IOException
	{
		Map<String, String> nsMap = NS_MAP;
		MockWebServiceMessageSender sender = (MockWebServiceMessageSender)new WsMockControl().expectRequest("xml/control-message-test.xml")
																					.failIf("//ns:number!=1",nsMap).returnResponse("mock-responses/test/default-response.xml").createMock();
		assertNotNull(sender);
		assertEquals(3, sender.getRequestProcessors().size());
		
	
		WebServiceTemplate template = new WebServiceTemplate();
		template.setMessageSender(sender);
		StringResult responseResult = new StringResult();
		template.sendSourceAndReceiveToResult(URI,createMessage("xml/valid-message.xml").getPayloadSource(), responseResult );
	}
	@Test(expected=WsTestException.class)
	public void testXPathAssertion() throws IOException
	{
		MockWebServiceMessageSender sender = (MockWebServiceMessageSender)new WsMockControl().expectRequest("xml/control-message-test.xml")
														.assertThat("//ns:number=1",NS_MAP).returnResponse("mock-responses/test/default-response.xml").createMock();
		assertNotNull(sender);
		assertEquals(3, sender.getRequestProcessors().size());
		
		
		WebServiceTemplate template = new WebServiceTemplate();
		template.setMessageSender(sender);
		StringResult responseResult = new StringResult();
		template.sendSourceAndReceiveToResult(URI,createMessage("xml/valid-message.xml").getPayloadSource(), responseResult );
	}
	
	@Test(expected=WsTestException.class)
	public void testExpectAndThrow() throws IOException
	{
		MockWebServiceMessageSender sender = (MockWebServiceMessageSender)new WsMockControl().expectRequest("xml/control-message-test.xml").throwException(new WsTestException("Test error")).createMock();
		assertNotNull(sender);
		assertEquals(2, sender.getRequestProcessors().size());
		
		ExpressionBasedResourceLookup lookup1 = (ExpressionBasedResourceLookup)((XmlCompareRequestValidator)extractRequestProcessor(sender, 0)).getControlResourceLookup();
		assertEquals("xml/control-message-test.xml", lookup1.getResourceExpressions()[0]);
			
		WebServiceTemplate template = new WebServiceTemplate();
		template.setMessageSender(sender);
		StringResult responseResult = new StringResult();
		template.sendSourceAndReceiveToResult(URI,createMessage("xml/valid-message.xml").getPayloadSource(), responseResult );
	}
	
	@Test(expected=IllegalStateException.class)
	public void testCreateEmpty()
	{
		new WsMockControl().createMock();
	}
	
	
	@Test(expected=WsTestException.class)
	public void testVerifyNoCall()
	{
		WsMockControl control = new WsMockControl().throwException(new WsTestException("Test error"));
		control.verify();
	}

}