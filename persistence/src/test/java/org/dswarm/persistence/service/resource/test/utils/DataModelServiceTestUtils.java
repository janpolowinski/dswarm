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
package org.dswarm.persistence.service.resource.test.utils;

import org.junit.Assert;

import org.dswarm.persistence.model.resource.Configuration;
import org.dswarm.persistence.model.resource.DataModel;
import org.dswarm.persistence.model.resource.Resource;
import org.dswarm.persistence.model.resource.ResourceType;
import org.dswarm.persistence.model.resource.proxy.ProxyDataModel;
import org.dswarm.persistence.model.schema.Schema;
import org.dswarm.persistence.service.resource.DataModelService;
import org.dswarm.persistence.service.schema.test.utils.SchemaServiceTestUtils;
import org.dswarm.persistence.service.test.utils.ExtendedBasicDMPJPAServiceTestUtils;

public class DataModelServiceTestUtils extends ExtendedBasicDMPJPAServiceTestUtils<DataModelService, ProxyDataModel, DataModel> {

	private final ResourceServiceTestUtils		resourcesServiceTestUtils;

	private final ConfigurationServiceTestUtils	configurationsServiceTestUtils;

	private final SchemaServiceTestUtils		schemasServiceTestUtils;

	public DataModelServiceTestUtils() {

		super(DataModel.class, DataModelService.class);

		resourcesServiceTestUtils = new ResourceServiceTestUtils();
		configurationsServiceTestUtils = new ConfigurationServiceTestUtils();
		schemasServiceTestUtils = new SchemaServiceTestUtils();
	}

	/**
	 * {@inheritDoc} <br />
	 * Assert that either both data models have no {@link Resource} or their resources are equal, see
	 * {@link ResourceServiceTestUtils#compareObjects(Resource, Resource)}. <br />
	 * Assert that either both data models have no {@link Configuration} or their configurations are equal, see
	 * {@link ConfigurationServiceTestUtils#compareObjects(Configuration, Configuration)}. <br />
	 * Assert that either both data models have no {@link Schema} or their schemata are equal, see
	 * {@link SchemaServiceTestUtils#compareObjects(Schema, Schema)}. <br />
	 */
	@Override
	public void compareObjects(final DataModel expectedDataModel, final DataModel actualDataModel) {

		super.compareObjects(expectedDataModel, actualDataModel);

		// TODO: re-enable reference deserializer or manually retrieve objects by from DB

		// check resource
		if (expectedDataModel.getDataResource() == null) {

			Assert.assertNull("the actual data model shouldn't have a resource", actualDataModel.getDataResource());

		} else {
			resourcesServiceTestUtils.compareObjects(expectedDataModel.getDataResource(), actualDataModel.getDataResource());
		}

		// check configuration
		if (expectedDataModel.getConfiguration() == null) {

			Assert.assertNull("the actual data model shouldn't have a configuration", actualDataModel.getConfiguration());

		} else {
			configurationsServiceTestUtils.compareObjects(expectedDataModel.getConfiguration(), actualDataModel.getConfiguration());
		}

		// check schema
		if (expectedDataModel.getSchema() == null) {

			Assert.assertNull("the actual data model shouldn't have a schema", actualDataModel.getSchema());

		} else {
			schemasServiceTestUtils.compareObjects(expectedDataModel.getSchema(), actualDataModel.getSchema());
		}
	}

	/**
	 * {@inheritDoc}<br/>
	 * Updates the name, description, resource, configuration and schema of the data model.
	 */
	@Override
	protected DataModel prepareObjectForUpdate(final DataModel objectWithUpdates, final DataModel object) {

		super.prepareObjectForUpdate(objectWithUpdates, object);

		object.setDataResource(objectWithUpdates.getDataResource());
		object.setConfiguration(objectWithUpdates.getConfiguration());
		object.setSchema(objectWithUpdates.getSchema());

		return object;
	}

	@Override
	public void reset() {

		schemasServiceTestUtils.reset();
		resourcesServiceTestUtils.reset();
		configurationsServiceTestUtils.reset();
	}
	
	/**
	 * A simple internal model example (using bibo:Document)
	 * 
	 * @return
	 * @throws Exception
	 */
	public DataModel getExampleDataModel1() throws Exception {
		
		DataModel dataModel = new DataModel();
		dataModel.setName("my output data model");
		dataModel.setDescription("my output data model description");
		
		dataModel.setSchema(schemasServiceTestUtils.getExampleSchema1());
		
		DataModel updatedDataModel = createObject(dataModel, dataModel);
		
		return updatedDataModel;
	}

	/**
	 * A mab-XML example with a file resource and configuration for http://www.ddb.de/professionell/mabxml/mabxml-1.xsd
	 * 
	 * @return
	 * @throws Exception
	 */
	public DataModel getExampleDataModel2() throws Exception {
		
		DataModel dataModel = new DataModel();
		//dataModel.setName("my input data model");
		//dataModel.setDescription("my input data model description");
		
		Resource resource = resourcesServiceTestUtils.createResource("test-mabxml.xml", null, ResourceType.FILE, null, null);
		
		Configuration configuration = configurationsServiceTestUtils.getExampleConfiguration1(resource);
		
		dataModel.setConfiguration(configuration);
		dataModel.setDataResource(resource);
		
		DataModel updatedDataModel = createObject(dataModel, dataModel);
		
		return updatedDataModel;
	}
}
