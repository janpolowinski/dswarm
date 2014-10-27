/**
 * Copyright (C) 2013, 2014 SLUB Dresden & Avantgarde Labs GmbH (<code@dswarm.org>)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dswarm.persistence.model.job.proxy;

import javax.xml.bind.annotation.XmlRootElement;

import org.dswarm.persistence.model.job.Task;
import org.dswarm.persistence.model.proxy.ProxyBasicDMPJPAObject;
import org.dswarm.persistence.model.proxy.RetrievalType;

/**
 * A proxy class for {@link Tasks}s.
 * 
 * @author polowins
 */
@XmlRootElement
public class ProxyTask extends ProxyBasicDMPJPAObject<Task> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * Default constructor for handing over a freshly created task, i.e., no updated or already existing task.
	 * 
	 * @param taskArg a freshly created task
	 */
	public ProxyTask(final Task taskArg) {

		super(taskArg);
	}

	/**
	 * Creates a new proxy with the given real task and the type how the task was processed by the task persistence
	 * service, e.g., {@link RetrievalType.CREATED}.
	 * 
	 * @param taskArg a task that was processed by the task persistence service
	 * @param typeArg the type how this task was processed by the task persistence service
	 */
	public ProxyTask(final Task taskArg, final RetrievalType typeArg) {

		super(taskArg, typeArg);
	}
}
