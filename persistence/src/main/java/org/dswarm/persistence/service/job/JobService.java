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
package org.dswarm.persistence.service.job;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;

import org.dswarm.persistence.model.job.Job;
import org.dswarm.persistence.model.job.proxy.ProxyJob;
import org.dswarm.persistence.service.BasicDMPJPAService;

/**
 * A persistence service for {@link Job}s.
 * 
 * @author polowins
 */
public class JobService extends BasicDMPJPAService<ProxyJob, Job> {

	/**
	 * Creates a new job persistence service with the given entity manager provider.
	 * 
	 * @param entityManagerProvider an entity manager provider
	 */
	@Inject
	public JobService(final Provider<EntityManager> entityManagerProvider) {

		super(Job.class, ProxyJob.class, entityManagerProvider);
	}

	@Override
	protected void prepareObjectForRemoval(Job object) {
		// TODO Auto-generated method stub
		
	}

}
