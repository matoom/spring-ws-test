package net.krecan.springws.test;

import java.net.URI;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.WebServiceMessageFactory;

/**
 * Generates mock response.
 * @author Lukas Krecan
 *
 */
public interface ResponseGenerator {
	public WebServiceMessage generateResponse(URI uri, WebServiceMessageFactory messageFactory, WebServiceMessage request);
}
