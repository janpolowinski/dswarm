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
package org.dswarm.persistence.service.job.test.utils;

import org.dswarm.persistence.model.job.Job;
import org.dswarm.persistence.model.job.proxy.ProxyJob;
import org.dswarm.persistence.service.job.JobService;
import org.dswarm.persistence.service.test.utils.BasicDMPJPAServiceTestUtils;

/**
 * @author polowins
 *
 */
public class JobServiceTestUtils extends BasicDMPJPAServiceTestUtils<JobService, ProxyJob, Job> {



	public JobServiceTestUtils() {

		super(Job.class, JobService.class);

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	public Job createJob(final String name, final  String description) throws Exception {

		final Job job = new Job();
		
		if (null == name) {
			job.setName(name);
		}
		if (null == description) {
			job.setDescription(description);
		}
		
		final Job updatedJob = createObject(job, job);

		return updatedJob;
	}

}
