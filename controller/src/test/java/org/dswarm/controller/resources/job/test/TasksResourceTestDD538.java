/**
 * Copyright (C) 2013 – 2015 SLUB Dresden & Avantgarde Labs GmbH (<code@dswarm.org>)
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
package org.dswarm.controller.resources.job.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dswarm.controller.resources.job.TasksResource;
import org.dswarm.controller.resources.resource.DataModelsResource;
import org.dswarm.controller.resources.resource.test.utils.DataModelsResourceTestUtils;
import org.dswarm.controller.resources.resource.test.utils.ResourcesResourceTestUtils;
import org.dswarm.controller.resources.test.ResourceTest;
import org.dswarm.controller.test.GuicedTest;
import org.dswarm.persistence.model.resource.Configuration;
import org.dswarm.persistence.model.resource.DataModel;
import org.dswarm.persistence.model.resource.Resource;
import org.dswarm.persistence.model.resource.ResourceType;
import org.dswarm.persistence.model.resource.utils.ConfigurationStatics;
import org.dswarm.persistence.model.resource.utils.DataModelUtils;
import org.dswarm.persistence.service.UUIDService;
import org.dswarm.persistence.util.DMPPersistenceUtil;

public abstract class TasksResourceTestDD538 extends ResourceTest {

	private static final Logger LOG = LoggerFactory.getLogger(TasksResourceTestDD538.class);

	protected String taskJSONString = null;

	protected ResourcesResourceTestUtils resourcesResourceTestUtils;

	protected DataModelsResourceTestUtils dataModelsResourceTestUtils;

	private final ObjectMapper objectMapper = GuicedTest.injector.getInstance(ObjectMapper.class);

	private final String taskJSONFileName;
	private final String inputDataResourceFileName;
	private final String testPostfix;
	private final String recordTag;
	private final String storageType;
	private final String expectedResultXMLFileName;

	public TasksResourceTestDD538(final String taskJSONFileNameArg, final String inputDataResourceFileNameArg, final String recordTagArg,
			final String storageTypeArg, final String expectedResultXMLFileNameArg, final String testPostfixArg) {

		super("tasks");

		taskJSONFileName = taskJSONFileNameArg;
		inputDataResourceFileName = inputDataResourceFileNameArg;
		recordTag = recordTagArg;
		storageType = storageTypeArg;
		expectedResultXMLFileName = expectedResultXMLFileNameArg;
		testPostfix = testPostfixArg;
	}

	@Override protected void initObjects() {
		super.initObjects();

		resourcesResourceTestUtils = new ResourcesResourceTestUtils();
		dataModelsResourceTestUtils = new DataModelsResourceTestUtils();
	}

	@Override
	public void prepare() throws Exception {

		super.prepare();

		taskJSONString = DMPPersistenceUtil.getResourceAsString(taskJSONFileName);
	}

	/**
	 * note: the result XML might not be what one would expect, because it contains for feld->nr multiple values, whereby each value is encapsulated in an own mabxml:nr element (i.e. this isn't conform to the MABXML schema)
	 *
	 * @throws Exception
	 */
	@Test
	public void testTaskExecution() throws Exception {

		TasksResourceTestDD538.LOG.debug("start DD-538 {} task execution test", testPostfix);

		final PrepareResource prepareResource = new PrepareResource(inputDataResourceFileName).invoke();
		final PrepareConfiguration prepareConfiguration = new PrepareConfiguration(prepareResource, recordTag, storageType).invoke();
		DataModel inputDataModel = prepareDataModel(prepareResource, prepareConfiguration);

		final ObjectNode requestJSON = prepareTask(inputDataModel);

		final Response response = target().request(MediaType.APPLICATION_XML_TYPE).post(Entity.json(requestJSON));

		Assert.assertEquals("200 Created was expected", 200, response.getStatus());

		final String actualResultXML = response.readEntity(String.class);

		Assert.assertNotNull("the response JSON shouldn't be null", actualResultXML);

		TasksResourceTestDD538.LOG.debug("task execution response = '{}'", actualResultXML);

		final String expectedResultXML = DMPPersistenceUtil.getResourceAsString(expectedResultXMLFileName);

		Assert.assertEquals(expectedResultXML.length(), actualResultXML.length());

		TasksResourceTestDD538.LOG.debug("end DD-538 {} task execution test", testPostfix);
	}

	private ObjectNode prepareTask(final DataModel inputDataModel) throws Exception {

		// manipulate input data model
		final String finalInputDataModelJSONString = objectMapper.writeValueAsString(inputDataModel);
		final ObjectNode finalInputDataModelJSON = objectMapper.readValue(finalInputDataModelJSONString, ObjectNode.class);

		final ObjectNode taskJSON = objectMapper.readValue(taskJSONString, ObjectNode.class);
		taskJSON.set("input_data_model", finalInputDataModelJSON);

		final ObjectNode requestJSON = objectMapper.createObjectNode();
		requestJSON.set(TasksResource.TASK_IDENTIFIER, taskJSON);
		requestJSON.put(TasksResource.PERSIST_IDENTIFIER, Boolean.FALSE);
		requestJSON.put(TasksResource.DO_INGEST_ON_THE_FLY_IDENTIFIER, Boolean.TRUE);
		requestJSON.put(TasksResource.DO_EXPORT_ON_THE_FLY_IDENTIFIER, Boolean.TRUE);
		requestJSON.put(TasksResource.DO_VERSIONING_ON_RESULT_IDENTIFIER, Boolean.FALSE);
		requestJSON.put(TasksResource.RETURN_IDENTIFIER, Boolean.TRUE);

		return requestJSON;
	}

	private DataModel prepareDataModel(final PrepareResource prepareResource, final PrepareConfiguration prepareConfiguration) throws Exception {

		final Resource resource = prepareResource.getResource();
		final Resource res1 = prepareResource.getRes1();
		final Configuration conf1 = prepareConfiguration.getConf1();
		final Configuration configuration = prepareConfiguration.getConfiguration();

		final String dataModel1Uuid = UUIDService.getUUID(DataModel.class.getSimpleName());

		final DataModel data1 = new DataModel(dataModel1Uuid);
		data1.setName("'" + res1.getName() + "' + '" + conf1.getName() + "' data model");
		data1.setDescription("data model of resource '" + res1.getName() + "' and configuration '" + conf1.getName() + "'");
		data1.setDataResource(resource);
		data1.setConfiguration(configuration);

		final String inputDataModelJSONString = objectMapper.writeValueAsString(data1);

		final WebTarget resourceTarget = dataModelsResourceTestUtils.getResourceTarget();
		final Response response = resourceTarget.queryParam(DataModelsResource.DO_INGEST_IDENTIFIER, Boolean.FALSE)
				.request(MediaType.APPLICATION_JSON_TYPE).post(
						Entity.json(inputDataModelJSONString));

		Assert.assertNotNull(response);
		Assert.assertEquals(201, response.getStatus());

		final String responseString = response.readEntity(String.class);

		Assert.assertNotNull("the response JSON shouldn't be null", responseString);

		final DataModel inputDataModel = dataModelsResourceTestUtils.readObject(responseString);

		Assert.assertNotNull("the data model shouldn't be null", inputDataModel);

		return inputDataModel;
	}

	private class PrepareResource {

		private       Resource res1;
		private       Resource resource;
		private final String   resourceFileName;

		public PrepareResource(final String resourceFileName) {

			this.resourceFileName = resourceFileName;
		}

		public Resource getRes1() {

			return res1;
		}

		public Resource getResource() {

			return resource;
		}

		public PrepareResource invoke() throws Exception {

			final String resource1Uuid = UUIDService.getUUID(Resource.class.getSimpleName());

			final int lastBackslash = resourceFileName.lastIndexOf("/");
			final String relativeResourceFileName= resourceFileName.substring(lastBackslash + 1, resourceFileName.length());

			res1 = new Resource(resource1Uuid);
			res1.setName(relativeResourceFileName);
			res1.setDescription("this is a description");
			res1.setType(ResourceType.FILE);

			final URL fileURL = Resources.getResource(resourceFileName);
			final File resourceFile = FileUtils.toFile(fileURL);

			final ObjectNode attributes1 = new ObjectNode(objectMapper.getNodeFactory());
			attributes1.put("path", resourceFile.getAbsolutePath());

			String fileType = null;
			final Tika tika = new Tika();
			try {
				fileType = tika.detect(resourceFile);
				// fileType = Files.probeContentType(resourceFile.toPath());
			} catch (final IOException e1) {

				TasksResourceTestDD538.LOG.debug("couldn't determine file type from file '{}'", resourceFile.getAbsolutePath());
			}

			if (fileType != null) {

				attributes1.put("filetype", fileType);
			}

			// hint: size is not important to know since its value is skipped in the comparison of actual and expected resource
			attributes1.put("filesize", -1);

			res1.setAttributes(attributes1);

			// upload data resource
			resource = resourcesResourceTestUtils.uploadResource(resourceFile, res1);

			return this;
		}
	}

	private class PrepareConfiguration {

		private       Configuration conf1;
		private       Configuration configuration;
		private final Resource      resource;
		private final String        recordTag;
		private final String        storageType;

		public PrepareConfiguration(final PrepareResource prepareResource, final String recordTagArg, final String storageTypeArg) {

			this.resource = prepareResource.getResource();
			recordTag = recordTagArg;
			storageType = storageTypeArg;
		}

		public Configuration getConf1() {

			return conf1;
		}

		public Configuration getConfiguration() {

			return configuration;
		}

		public PrepareConfiguration invoke() throws Exception {

			final String configuration1Uuid = UUIDService.getUUID(Configuration.class.getSimpleName());

			// process input data model
			conf1 = new Configuration(configuration1Uuid);

			conf1.setName("configuration " + testPostfix);
			conf1.addParameter(ConfigurationStatics.RECORD_TAG, new TextNode(recordTag));
			conf1.addParameter(ConfigurationStatics.STORAGE_TYPE, new TextNode(storageType));

			final String configurationJSONString = objectMapper.writeValueAsString(conf1);

			// create configuration
			configuration = resourcesResourceTestUtils.addResourceConfiguration(resource, configurationJSONString);
			return this;
		}
	}
}
