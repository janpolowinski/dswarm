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
package org.dswarm.persistence.service.job.test;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dswarm.persistence.GuicedTest;
import org.dswarm.persistence.model.job.Component;
import org.dswarm.persistence.model.job.Function;
import org.dswarm.persistence.model.job.Task;
import org.dswarm.persistence.model.job.Transformation;
import org.dswarm.persistence.model.job.proxy.ProxyTask;
import org.dswarm.persistence.model.schema.Attribute;
import org.dswarm.persistence.model.schema.AttributePath;
import org.dswarm.persistence.model.schema.MappingAttributePathInstance;
import org.dswarm.persistence.service.job.TaskService;
import org.dswarm.persistence.service.job.test.utils.ComponentServiceTestUtils;
import org.dswarm.persistence.service.job.test.utils.FunctionServiceTestUtils;
import org.dswarm.persistence.service.job.test.utils.TaskServiceTestUtils;
import org.dswarm.persistence.service.job.test.utils.TransformationServiceTestUtils;
import org.dswarm.persistence.service.schema.test.utils.AttributePathServiceTestUtils;
import org.dswarm.persistence.service.schema.test.utils.AttributeServiceTestUtils;
import org.dswarm.persistence.service.schema.test.utils.MappingAttributePathInstanceServiceTestUtils;
import org.dswarm.persistence.service.test.IDBasicJPAServiceTest;

/**
 * @author polowins
 *
 */
public class TaskServiceTest extends IDBasicJPAServiceTest<ProxyTask, Task, TaskService> {

	private static final Logger									LOG								= LoggerFactory.getLogger(TaskServiceTest.class);

	private ObjectMapper										objectMapper;

	private final Map<Long, Function>							functions						= Maps.newLinkedHashMap();

	private final Map<Long, Attribute>							attributes						= Maps.newLinkedHashMap();

	private final Map<Long, AttributePath>						attributePaths					= Maps.newLinkedHashMap();

	private final Map<Long, Component>							components						= Maps.newLinkedHashMap();

	private final Map<Long, Transformation>						transformations					= Maps.newLinkedHashMap();

	private final Map<Long, MappingAttributePathInstance>		mappingAttributePathInstances	= Maps.newLinkedHashMap();

	private AttributeServiceTestUtils						attributeServiceTestUtils;
	private AttributePathServiceTestUtils					attributePathServiceTestUtils;
	private FunctionServiceTestUtils						functionServiceTestUtils;
	private MappingAttributePathInstanceServiceTestUtils	mappingAttributePathInstanceServiceTestUtils;
	private ComponentServiceTestUtils						componentServiceTestUtils;
	private TransformationServiceTestUtils					transformationServiceTestUtils;
	private TaskServiceTestUtils							taskServiceTestUtils;

	public TaskServiceTest() {

		super("task", TaskService.class);

		initObjects();
	}
	
	@Override
	protected void initObjects() {

		super.initObjects();

		objectMapper = GuicedTest.injector.getInstance(ObjectMapper.class);

		attributeServiceTestUtils = new AttributeServiceTestUtils();
		attributePathServiceTestUtils = new AttributePathServiceTestUtils();
		functionServiceTestUtils = new FunctionServiceTestUtils();
		mappingAttributePathInstanceServiceTestUtils = new MappingAttributePathInstanceServiceTestUtils();
		componentServiceTestUtils = new ComponentServiceTestUtils();
		transformationServiceTestUtils = new TransformationServiceTestUtils();
		taskServiceTestUtils = new TaskServiceTestUtils();
	}
	
	private void resetObjectVars() {
		
		functions.clear();
		attributes.clear();
		attributePaths.clear();
		components.clear();
		transformations.clear();
		mappingAttributePathInstances.clear();
	}
	
	@Override
	public void prepare() throws Exception {

		GuicedTest.tearDown();
		GuicedTest.startUp();
		initObjects();
		resetObjectVars();
		super.prepare();
	}

	@Test
	public void simpleTaskTest() throws Exception {

		TaskServiceTest.LOG.debug("start simple task test");

		
		TaskServiceTest.LOG.debug("end simple task test");
	}
	
	@Test
	public void example() throws Exception {
		taskServiceTestUtils.exampleTask1();
	}
}
