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
package net.javacrumbs.springws.test.simple.annotation;

import net.javacrumbs.springws.test.util.MockMessageSenderInjector;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.WebServiceMessageSender;

/**
 * Listener alters current application context. It registers {@link ThreadLocalWsMockControlFactoryBean} and replaces all
 * {@link WebServiceMessageSender}s in all {@link WebServiceTemplate}s in the application context.
 * @author Lukas Krecan
 *
 */
public class WsMockControlTestExecutionListener extends AbstractTestExecutionListener {

	private static final String MESSAGE_SENDER_BEAN_NAME = "net.javacrumbs.springws.test.simple.annotation.WsMockControlMockWebServiceMessageSender";
	private static final String MOCK_CONTROL_BEAN_NAME = "net.javacrumbs.springws.test.simple.annotation.WsMockControl";

	@Override
	public void prepareTestInstance(TestContext testContext) throws Exception {
		ConfigurableListableBeanFactory beanFactory = ((AbstractApplicationContext)testContext.getApplicationContext()).getBeanFactory();
		
		if (!beanFactory.containsBean(MOCK_CONTROL_BEAN_NAME))
		{
			beanFactory.registerSingleton(MOCK_CONTROL_BEAN_NAME, new ThreadLocalWsMockControlFactoryBean());
			WsMockControlMockWebServiceMessageSender messageSender = new WsMockControlMockWebServiceMessageSender();
			beanFactory.registerSingleton(MESSAGE_SENDER_BEAN_NAME, messageSender);
			new MockMessageSenderInjector().postProcessBeanFactory(beanFactory);
		}

	}
	
	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		ThreadLocalWsMockControlFactoryBean.clean();
	}
}
