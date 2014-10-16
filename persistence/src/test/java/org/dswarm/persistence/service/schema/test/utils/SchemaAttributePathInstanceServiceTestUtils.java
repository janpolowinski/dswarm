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
package org.dswarm.persistence.service.schema.test.utils;

import org.junit.Assert;

import org.dswarm.persistence.model.schema.AttributePath;
import org.dswarm.persistence.model.schema.Schema;
import org.dswarm.persistence.model.schema.SchemaAttributePathInstance;
import org.dswarm.persistence.model.schema.proxy.ProxySchemaAttributePathInstance;
import org.dswarm.persistence.service.schema.SchemaAttributePathInstanceService;

public class SchemaAttributePathInstanceServiceTestUtils extends
		AttributePathInstanceServiceTestUtils<SchemaAttributePathInstanceService, ProxySchemaAttributePathInstance, SchemaAttributePathInstance> {

	//private final FilterServiceTestUtils	filtersResourceTestUtils;

	public SchemaAttributePathInstanceServiceTestUtils() {

		super(SchemaAttributePathInstance.class, SchemaAttributePathInstanceService.class);

		//filtersResourceTestUtils = new FilterServiceTestUtils();
	}

	/**
	 * {@inheritDoc}<br />
	 * 
	 * @param expectedSchemaAttributePathInstance
	 * @param actualSchemaAttributePathInstance
	 */
	@Override
	public void compareObjects(final SchemaAttributePathInstance expectedSchemaAttributePathInstance,
			final SchemaAttributePathInstance actualSchemaAttributePathInstance) {

		super.compareObjects(expectedSchemaAttributePathInstance, actualSchemaAttributePathInstance);

//		Assert.assertEquals("the ordinals of the schema attribute path should be equal", expectedSchemaAttributePathInstance.getOrdinal(),
//				actualSchemaAttributePathInstance.getOrdinal());

//		if (expectedSchemaAttributePathInstance.getFilter() == null) {
//
//			Assert.assertNull("the actual schema attribute path instance should not have a filter", actualSchemaAttributePathInstance.getFilter());
//
//		} else {
//
//			filtersResourceTestUtils.compareObjects(expectedSchemaAttributePathInstance.getFilter(), actualSchemaAttributePathInstance.getFilter());
//		}
	}

	public SchemaAttributePathInstance createSchemaAttributePathInstance(final String name, final AttributePath attributePath,
			final Schema subSchema) throws Exception {

		final SchemaAttributePathInstance schemaAttributePathInstance = new SchemaAttributePathInstance();

		schemaAttributePathInstance.setName(name);
		schemaAttributePathInstance.setAttributePath(attributePath);
		schemaAttributePathInstance.setSubSchema(subSchema);
//		schemaAttributePathInstance.setOrdinal(ordinal);
//		schemaAttributePathInstance.setFilter(filter);

		final SchemaAttributePathInstance updatedSchemaAttributePathInstance = createObject(schemaAttributePathInstance,
				schemaAttributePathInstance);

		Assert.assertNotNull(updatedSchemaAttributePathInstance.getId());

		return updatedSchemaAttributePathInstance;
	}

	/**
	 * {@inheritDoc}<br/>
	 */
	@Override
	protected SchemaAttributePathInstance prepareObjectForUpdate(final SchemaAttributePathInstance objectWithUpdates,
			final SchemaAttributePathInstance object) {

		super.prepareObjectForUpdate(objectWithUpdates, object);

		object.setSubSchema(objectWithUpdates.getSubSchema());
//		object.setFilter(objectWithUpdates.getFilter());
//		object.setOrdinal(objectWithUpdates.getOrdinal());

		return object;
	}

	@Override
	public void reset() {

		super.reset();
		//filtersResourceTestUtils.reset();
	}
}
