package net.javacrumbs.springws.test.simple;

import java.io.IOException;
import java.net.URI;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.WebServiceMessageFactory;

import net.javacrumbs.springws.test.RequestProcessor;
import net.javacrumbs.springws.test.WsTestException;

/**
 * Request processor wrapper that verifies number of {@link #processRequest(URI, WebServiceMessageFactory, WebServiceMessage)} calls. 
 * @author Lukas Krecan
 *
 */
public class VerifiableRequestProcessorWrapper implements RequestProcessor, VerifiableRequestProcessor {

	private final RequestProcessor wrappedRequestProcessor;
	
	private int numberOfProcessedRequests = 0;
	
	private int minNumberOfProcessedRequests = 1;
	
	private int maxNumberOfProcessedRequests = 1;
	
	private final String requestProcessorDescription;
	
	public VerifiableRequestProcessorWrapper(RequestProcessor wrappedRequestProcessor, String requestProcessorDescription) {
		this.wrappedRequestProcessor = wrappedRequestProcessor;
		this.requestProcessorDescription = requestProcessorDescription;
	}



	public WebServiceMessage processRequest(URI uri, WebServiceMessageFactory messageFactory, WebServiceMessage request)
			throws IOException {
		numberOfProcessedRequests++;
		if (numberOfProcessedRequests>maxNumberOfProcessedRequests)
		{
			throw new WsTestException(generateErrorMessage());
		}
		return wrappedRequestProcessor.processRequest(uri, messageFactory, request);
	}



	public int getNumberOfProcessedRequests() {
		return numberOfProcessedRequests;
	}

	/* (non-Javadoc)
	 * @see net.javacrumbs.springws.test.simple.VerifiableRequestProcessor#verify()
	 */
	public void verify() throws WsTestException{
		if (numberOfProcessedRequests>maxNumberOfProcessedRequests || numberOfProcessedRequests<minNumberOfProcessedRequests)
		{
			throw new WsTestException(generateErrorMessage());
		}
	}



	private String generateErrorMessage() {
		return requestProcessorDescription + ": Unexpected call, expected from "+minNumberOfProcessedRequests+" to "+maxNumberOfProcessedRequests+" calls, was "+numberOfProcessedRequests+".";
	}


	public RequestProcessor getWrappedRequestProcessor() {
		return wrappedRequestProcessor;
	}

	/* (non-Javadoc)
	 * @see net.javacrumbs.springws.test.simple.VerifiableRequestProcessor#getMinNumberOfProcessedRequests()
	 */
	public int getMinNumberOfProcessedRequests() {
		return minNumberOfProcessedRequests;
	}



	/* (non-Javadoc)
	 * @see net.javacrumbs.springws.test.simple.VerifiableRequestProcessor#setMinNumberOfProcessedRequests(int)
	 */
	public void setMinNumberOfProcessedRequests(int minNumberOfProcessedRequests) {
		this.minNumberOfProcessedRequests = minNumberOfProcessedRequests;
	}



	/* (non-Javadoc)
	 * @see net.javacrumbs.springws.test.simple.VerifiableRequestProcessor#getMaxNumberOfProcessedRequests()
	 */
	public int getMaxNumberOfProcessedRequests() {
		return maxNumberOfProcessedRequests;
	}



	/* (non-Javadoc)
	 * @see net.javacrumbs.springws.test.simple.VerifiableRequestProcessor#setMaxNumberOfProcessedRequests(int)
	 */
	public void setMaxNumberOfProcessedRequests(int maxNumberOfProcessedRequests) {
		this.maxNumberOfProcessedRequests = maxNumberOfProcessedRequests;
	}



	public void setNumberOfProcessedRequests(int numberOfProcessedRequests) {
		this.numberOfProcessedRequests = numberOfProcessedRequests;
	}



	String getRequestProcessorDescription() {
		return requestProcessorDescription;
	}




}
