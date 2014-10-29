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
package org.dswarm.persistence.service.schema.test.internalmodel;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dswarm.persistence.DMPPersistenceException;
import org.dswarm.persistence.GuicedTest;
import org.dswarm.persistence.model.schema.Clasz;
import org.dswarm.persistence.model.schema.Schema;
import org.dswarm.persistence.model.schema.SchemaAttributePathInstance;
import org.dswarm.persistence.service.schema.SchemaService;

public abstract class SchemaBuilder extends GuicedTest {

	private final ObjectMapper	objectMapper	= GuicedTest.injector.getInstance(ObjectMapper.class);
	private static final Logger	LOG				= LoggerFactory.getLogger(SchemaBuilder.class);
	protected String			prefixPaths		= "";

	public SchemaBuilder() {
		super();
	}

	public abstract Schema buildSchema();

	protected Schema createSchema(final String name, final Set<SchemaAttributePathInstance> attributePathInstances, final Clasz recordClass) {

		final SchemaService schemaService = GuicedTest.injector.getInstance(SchemaService.class);

		assertNotNull("schema service shouldn't be null", schemaService);

		// create schema

		Schema schema = null;

		try {
			schema = schemaService.createObjectTransactional().getObject();
		} catch (final DMPPersistenceException e) {

			assertTrue("something went wrong while schema creation.\n" + e.getMessage(), false);
		}

		assertNotNull("schema shouldn't be null", schema);
		assertNotNull("schema id shouldn't be null", schema.getId());

		schema.setName(name);
		schema.setAttributePathInstances(attributePathInstances);
		schema.setRecordClass(recordClass);

		// update schema

		Schema updatedSchema = null;

		try {

			updatedSchema = schemaService.updateObjectTransactional(schema).getObject();
			
		} catch (final DMPPersistenceException e) {

			Assert.fail("something went wrong while updating the schema of id = '" + schema.getId() + "'");
		}
		
		String json = null;

		try {
			
			json = objectMapper.writeValueAsString(updatedSchema);
			
		} catch (final JsonProcessingException e) {

			e.printStackTrace();
		}

		SchemaBuilder.LOG.debug("schema json: " + json);

		return updatedSchema;
	}

	public String getPrefixPaths() {
		return prefixPaths;
	}

}
