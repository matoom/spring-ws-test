package net.krecan.springws.test.lookup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.krecan.springws.test.AbstractMessageTest;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


public class XPathResourceLookupTest extends AbstractMessageTest {
	private XPathResourceLookup resourceLookup;
	public XPathResourceLookupTest() {
		
		Map<String, String> namespaceMap = new HashMap<String, String>();
		namespaceMap.put("ns", "http://www.example.org/schema");
		namespaceMap.put("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
		
		
		resourceLookup = new XPathResourceLookup();
		String resourceXpath = "concat('mock-responses/',name(//soapenv:Body/*[1]),'/',//ns:text,'-response.xml')";
				
		String defaultXPath = "concat('mock-responses/',name(//soapenv:Body/*[1]),'/default-response.xml')";
		resourceLookup.setResourceXPaths(new String[]{resourceXpath, defaultXPath});
		resourceLookup.setNamespaceMap(namespaceMap);
	}
	
	@Test
	public void testEmpty() throws IOException
	{
		XPathResourceLookup emptyResourceLookup = new XPathResourceLookup();
		assertNull(emptyResourceLookup.lookupResource(null, createMessage("xml/valid-message.xml")));
	}
	@Test
	public void testNormal() throws IOException
	{
		Resource resource = resourceLookup.lookupResource(null, createMessage("xml/valid-message.xml"));
		assertEquals(new ClassPathResource("mock-responses/test/default-response.xml").getFile(), resource.getFile());
	}
	@Test
	public void testDifferent() throws IOException
	{
		Resource resource = resourceLookup.lookupResource(null, createMessage("xml/valid-message2.xml"));
		assertEquals(new ClassPathResource("mock-responses/test/different-response.xml").getFile(), resource.getFile());
	}
	@Test
	public void testDifferentTest2() throws IOException
	{
		Resource resource = resourceLookup.lookupResource(null, createMessage("xml/valid-message-test2.xml"));
		assertEquals(new ClassPathResource("mock-responses/test2/different-response.xml").getFile(), resource.getFile());
	}
	
}