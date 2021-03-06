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

package net.javacrumbs.springws.test.common;

import java.net.URI;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.javacrumbs.springws.test.expression.ExpressionResolverException;
import net.javacrumbs.springws.test.expression.WsTestXPathVariableResolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

public class XPathExpressionEvaluator implements ExpressionEvaluator {
	
	private final Log logger = LogFactory.getLog(getClass());

	public String evaluateExpression(Document document, String expression, URI uri, NamespaceContext namespaceContext) {
		XPathFactory factory = XPathFactory.newInstance();
		factory.setXPathVariableResolver(new WsTestXPathVariableResolver(uri));
		try {
			XPath xpath = factory.newXPath();
			if (namespaceContext!=null)
			{
				xpath.setNamespaceContext(namespaceContext);
			}
			String result = xpath.evaluate(expression, document);
			logger.debug("Expression \"" + expression + "\" resolved to \"" + result+"\"");
			return result;
		} catch (XPathExpressionException e) {
			throw new ExpressionResolverException("Could not evaluate XPath expression \"" + expression + "\" : \"" + e.getMessage()+"\"", e);
		}
	}

}
