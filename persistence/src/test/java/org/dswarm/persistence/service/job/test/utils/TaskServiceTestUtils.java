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
import org.dswarm.persistence.model.job.Task;
import org.dswarm.persistence.model.job.proxy.ProxyTask;
import org.dswarm.persistence.model.resource.DataModel;
import org.dswarm.persistence.model.schema.Schema;
import org.dswarm.persistence.service.job.TaskService;
import org.dswarm.persistence.service.resource.test.utils.DataModelServiceTestUtils;
import org.dswarm.persistence.service.schema.test.utils.SchemaServiceTestUtils;
import org.dswarm.persistence.service.test.utils.BasicDMPJPAServiceTestUtils;

/**
 * @author polowins
 *
 */
public class TaskServiceTestUtils extends BasicDMPJPAServiceTestUtils<TaskService, ProxyTask, Task> {



	private JobServiceTestUtils	jobServiceTestUtils = new JobServiceTestUtils();
	private SchemaServiceTestUtils	schemaServiceTestUtils = new SchemaServiceTestUtils();
	private DataModelServiceTestUtils dataModelsServiceTestUtils = new DataModelServiceTestUtils();

	public TaskServiceTestUtils() {

		super(Task.class, TaskService.class);

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	public Task exampleTask1() throws Exception {
		
		// tasks and jobs cannot yet be persisted
		/*
		Task task = createTask("my task", "my task description");
		
		Job job = jobServiceTestUtils.createJob(null, null);
		
		task.setJob(job);
		 
		final Task updatedTask = updateObject(task, task);
		*/
		
		Task task = new Task();
		task.setName("my task");
		task.setDescription("my task description");
		
		Job job  = new Job();
		//Schema outputSchema = schemaServiceTestUtils.getExampleSchema1();
		DataModel inputDataModel = dataModelsServiceTestUtils.getExampleDataModel2();
		DataModel outputDataModel = dataModelsServiceTestUtils.getExampleDataModel1();
		
		task.setJob(job);
		task.setInputDataModel(inputDataModel);
		task.setOutputDataModel(outputDataModel);
		
		return task;
	}

	public Task createTask(final String name, final  String description) throws Exception {

		final Task task = new Task();

		task.setName(name);
		task.setDescription(description);

		final Task updatedTask = createObject(task, task);

		return updatedTask;
	}

}
