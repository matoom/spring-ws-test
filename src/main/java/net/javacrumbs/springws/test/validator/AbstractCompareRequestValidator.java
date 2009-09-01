package net.javacrumbs.springws.test.validator;

import java.io.IOException;
import java.net.URI;

import net.javacrumbs.springws.test.lookup.ResourceLookup;
import net.javacrumbs.springws.test.util.DefaultXmlUtil;
import net.javacrumbs.springws.test.util.XmlUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.ws.WebServiceMessage;
import org.w3c.dom.Document;

public abstract class AbstractCompareRequestValidator implements InitializingBean{

	private ResourceLookup controlResourceLookup;
	protected final Log logger = LogFactory.getLog(getClass());
	private XmlUtil xmlUtil = DefaultXmlUtil.getInstance();

	public AbstractCompareRequestValidator() {
		super();
	}

	public void validateRequest(URI uri, WebServiceMessage message) throws IOException {
		Document messageDocument = loadDocument(message);
	
		Resource controlResource = controlResourceLookup.lookupResource(uri, message);
		if (controlResource!=null)
		{
			Document controlDocument = loadDocument(controlResource);
	
			compareDocuments(controlDocument, messageDocument);
		}
		else
		{
			logger.warn("Can not find resource to validate with.");
		}
	}

	/**
	 * Does the actual comparison.
	 * @param controlDocument
	 * @param messageDocument
	 */
	protected abstract void compareDocuments(Document controlDocument, Document messageDocument);
	
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(controlResourceLookup, "ControlResourceLookup has to be set");		
	}

	protected Document loadDocument(WebServiceMessage message) throws IOException {
		return getXmlUtil().loadDocument(message);
	}

	protected Document loadDocument(Resource resource) throws IOException {
		return getXmlUtil().loadDocument(resource);
	}

	public ResourceLookup getControlResourceLookup() {
		return controlResourceLookup;
	}

	public void setControlResourceLookup(ResourceLookup controlResourceLookup) {
		this.controlResourceLookup = controlResourceLookup;
	}

	public XmlUtil getXmlUtil() {
		return xmlUtil;
	}

	public void setXmlUtil(XmlUtil xmlUtil) {
		this.xmlUtil = xmlUtil;
	}

}