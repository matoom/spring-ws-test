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
package net.javacrumbs.springws.test.template;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.ws.WebServiceMessage;

/**
 * Evaluates template.
 * @author Lukas Krecan
 *
 */
public interface TemplateProcessor {
	/**
	 * Processes the resource as template. Returns resource with processed data. If the processor is not applicable 
	 * returns original resource or its copy.
	 * @param resource
	 * @param uri
	 * @param message Message to be used in template as input. Can be null
	 * @return
	 * @throws IOException 
	 */
	public Resource processTemplate(Resource resource, WebServiceMessage message) throws IOException;
}
