package net.javacrumbs.springws.test.simple;

import java.util.ArrayList;
import java.util.List;

import net.javacrumbs.springws.test.MockWebServiceMessageSender;
import net.javacrumbs.springws.test.ResponseGenerator;
import net.javacrumbs.springws.test.generator.DefaultResponseGenerator;
import net.javacrumbs.springws.test.lookup.DefaultResourceLookup;
import net.javacrumbs.springws.test.validator.XmlCompareRequestValidator;

import org.springframework.ws.transport.WebServiceMessageSender;

public class SimpleMessageFactory {
	private final List<ResponseGenerator> responseGenerators = new ArrayList<ResponseGenerator>();
	
	private WebServiceMessageSender create() {
		MockWebServiceMessageSender messageSender = new MockWebServiceMessageSender();
		messageSender.setResponseGenerators(responseGenerators);
		return messageSender;
	}

	public SimpleMessageFactory addResponseGenerator(ResponseGenerator responseGenerator)
	{
		responseGenerators.add(responseGenerator);
		return this;
	}
	

	public SimpleMessageFactory expectRequest(String resourceName) {
		XmlCompareRequestValidator validator = new XmlCompareRequestValidator();
		DefaultResourceLookup resourceLookup = new DefaultResourceLookup();
		resourceLookup.setResourceExpressions("'"+resourceName+"'");
		validator.setControlResourceLookup(resourceLookup);
		addResponseGenerator(validator);
		return this;
	}

	public WebServiceMessageSender andReturnResponse(String resourceName) {
		DefaultResponseGenerator responseGenerator = new DefaultResponseGenerator();
		DefaultResourceLookup resourceLookup = new DefaultResourceLookup();
		resourceLookup.setResourceExpressions("'"+resourceName+"'");
		responseGenerator.setResourceLookup(resourceLookup);
		addResponseGenerator(responseGenerator);
		return create();
	}
	
	

}
