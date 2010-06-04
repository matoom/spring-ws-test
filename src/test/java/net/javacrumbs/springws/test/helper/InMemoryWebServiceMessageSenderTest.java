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

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.net.URI;


import org.junit.Test;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.transport.WebServiceMessageReceiver;

public class InMemoryWebServiceMessageSenderTest {

	@Test
	public void testCreateConnection() throws Exception
	{

		WebServiceMessageFactory messageFactory = createMock(WebServiceMessageFactory.class);
		WebServiceMessageReceiver messageReceiver = createMock(WebServiceMessageReceiver.class);
		
		InMemoryWebServiceConnection connection = new InMemoryWebServiceMessageSender(messageFactory, messageReceiver).createConnection(new URI("http://localhost"));
		assertNotNull(connection);
		assertSame(messageFactory, connection.getMessageFactory());
		assertSame(messageReceiver, connection.getWebServiceMessageReceiver());
	}
}
