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
package net.javacrumbs.springws.test;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;
import org.springframework.ws.transport.WebServiceConnection;
import org.springframework.ws.transport.WebServiceMessageSender;

/**
 * Creates new {@link MockWebServiceConnection}. 
 * @author Lukas Krecan
 *
 */
public class MockWebServiceMessageSender implements WebServiceMessageSender, InitializingBean, ApplicationContextAware {
	
	private List<ResponseGenerator> responseGenerators = new ArrayList<ResponseGenerator>();
	
	private boolean autowireResponseGenerators = true;
	
	private ApplicationContext applicationContext;
	
	public WebServiceConnection createConnection(URI uri) throws IOException {
		MockWebServiceConnection connection = new MockWebServiceConnection(uri);
		connection.setResponseGenerators(getResponseGenerators());
		return connection;
	}

	public boolean supports(URI uri) {
		return true;
	}
	
	/**
	 * Response generators used to generate the response.
	 */
	public List<ResponseGenerator> getResponseGenerators() {
		return Collections.unmodifiableList(responseGenerators);
	}

	public void setResponseGenerators(List<ResponseGenerator> responseGenerators) {
		this.responseGenerators = new ArrayList<ResponseGenerator>(responseGenerators);
	}
	
	public void setResponseGenerator(ResponseGenerator responseGenerator) {
		setResponseGenerators(Collections.singletonList(responseGenerator));
	}

	public void afterPropertiesSet() {
		autowireResponseGenerators();
	}

	@SuppressWarnings("unchecked")
	private void autowireResponseGenerators() {
		if (isAutowireResponseGenerators())
		{
			List<ResponseGenerator> generators = new ArrayList<ResponseGenerator>(applicationContext.getBeansOfType(ResponseGenerator.class).values());
			Collections.sort(generators, new OrderComparator());
			responseGenerators.addAll(generators);
		}
	}

	public boolean isAutowireResponseGenerators() {
		return autowireResponseGenerators;
	}

	public void setAutowireResponseGenerators(boolean autowireResponseGenerators) {
		this.autowireResponseGenerators = autowireResponseGenerators;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;		
	}
}
