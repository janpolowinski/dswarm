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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.Assert;

import org.dswarm.persistence.DMPPersistenceException;
import org.dswarm.persistence.model.schema.Attribute;
import org.dswarm.persistence.model.schema.AttributePath;
import org.dswarm.persistence.model.schema.Clasz;
import org.dswarm.persistence.model.schema.Schema;
import org.dswarm.persistence.model.schema.SchemaAttributePathInstance;
import org.dswarm.persistence.model.schema.proxy.ProxySchema;
import org.dswarm.persistence.service.schema.SchemaService;
import org.dswarm.persistence.service.test.utils.BasicDMPJPAServiceTestUtils;

public class SchemaServiceTestUtils extends BasicDMPJPAServiceTestUtils<SchemaService, ProxySchema, Schema> {

	private final AttributePathServiceTestUtils	attributePathsServiceTestUtils;
	
	private final SchemaAttributePathInstanceServiceTestUtils	schemaAttributePathInstanceServiceTestUtils;

	private final ClaszServiceTestUtils			claszesServiceTestUtils;

	private final ContentSchemaServiceTestUtils	contentSchemaServiceTestUtils;

	private AttributeServiceTestUtils	attributesServiceTestUtils;
	

	public SchemaServiceTestUtils() {

		super(Schema.class, SchemaService.class);

		attributesServiceTestUtils = new AttributeServiceTestUtils();
		attributePathsServiceTestUtils = new AttributePathServiceTestUtils();
		schemaAttributePathInstanceServiceTestUtils = new SchemaAttributePathInstanceServiceTestUtils();
		claszesServiceTestUtils = new ClaszServiceTestUtils();
		contentSchemaServiceTestUtils = new ContentSchemaServiceTestUtils();
	}

	/**
	 * {@inheritDoc} <br />
	 * Assert that both {@link Schema}ta have either no or equal attribute paths, see
	 * {@link AttributePathServiceTestUtils#compareObjects(Set, Map)} for details.<br />
	 * Assert that both {@link Schema}ta have either no or equal record classes, see
	 * {@link ClaszServiceTestUtils#compareObjects(org.dswarm.persistence.model.DMPObject, org.dswarm.persistence.model.DMPObject)} for details.<br />
	 */
	@Override
	public void compareObjects(final Schema expectedSchema, final Schema actualSchema) {

		super.compareObjects(expectedSchema, actualSchema);

		if (expectedSchema.getUniqueAttributePaths() == null || expectedSchema.getUniqueAttributePaths().isEmpty()) {

			final boolean actualSchemaHasNoAttributePaths = (actualSchema.getUniqueAttributePaths() == null || actualSchema.getUniqueAttributePaths().isEmpty());
			Assert.assertTrue("the actual schema '" + actualSchema.getId() + "' shouldn't have attribute paths", actualSchemaHasNoAttributePaths);

		} else { // !null && !empty

			final Set<SchemaAttributePathInstance> actualAttributePaths = actualSchema.getUniqueAttributePaths();

			Assert.assertNotNull("attribute path instances of actual schema '" + actualSchema.getId() + "' shouldn't be null", actualAttributePaths);
			Assert.assertFalse("attribute path instances of actual schema '" + actualSchema.getId() + "' shouldn't be empty", actualAttributePaths.isEmpty());

			final Map<Long, SchemaAttributePathInstance> actualAttributePathsMap = Maps.newHashMap();

			for (final SchemaAttributePathInstance actualAttributePath : actualAttributePaths) {

				actualAttributePathsMap.put(actualAttributePath.getId(), actualAttributePath);
			}

			schemaAttributePathInstanceServiceTestUtils.compareObjects(expectedSchema.getUniqueAttributePaths(), actualAttributePathsMap);
		}

		if (expectedSchema.getRecordClass() == null) {

			Assert.assertNull("the actual schema '" + actualSchema.getId() + "' shouldn't have a record class", actualSchema.getRecordClass());

		} else {

			claszesServiceTestUtils.compareObjects(expectedSchema.getRecordClass(), actualSchema.getRecordClass());
		}

		if (expectedSchema.getContentSchema() != null) {

			contentSchemaServiceTestUtils.compareObjects(expectedSchema.getContentSchema(), actualSchema.getContentSchema());
		}
	}

	public Schema createSchema(final String name, final Set<SchemaAttributePathInstance> attributePaths, final Clasz recordClass) throws Exception {

		final Schema schema = new Schema();

		schema.setName(name);
		schema.setAttributePaths(attributePaths);
		schema.setRecordClass(recordClass);

		// update schema

		final Schema updatedSchema = createObject(schema, schema);

		Assert.assertNotNull("updated schema shouldn't be null", updatedSchema);
		Assert.assertNotNull("updated schema id shouldn't be null", updatedSchema.getId());

		return updatedSchema;
	}

	public void removeAddedAttributePathsFromOutputModelSchema(final Schema outputDataModelSchema, final Map<Long, Attribute> attributes,
			final Map<Long, SchemaAttributePathInstance> attributePaths) throws DMPPersistenceException {

		final Set<SchemaAttributePathInstance> outputDataModelSchemaAttributePathRemovalCandidates = Sets.newHashSet();

		// collect attribute paths of attributes that were created via processing the transformation result
		if (outputDataModelSchema != null) {

			final Set<SchemaAttributePathInstance> outputDataModelSchemaAttributePaths = outputDataModelSchema.getUniqueAttributePaths();

			if (outputDataModelSchemaAttributePaths != null) {

				for (final SchemaAttributePathInstance outputDataModelSchemaAttributePath : outputDataModelSchemaAttributePaths) {
					
					final AttributePath outputDataModelSchemaAttributePath2 = outputDataModelSchemaAttributePath.getAttributePath();

					final Set<Attribute> outputDataModelSchemaAttributePathAttributes = outputDataModelSchemaAttributePath2.getAttributes();

					for (final Attribute outputDataModelSchemaAttribute : outputDataModelSchemaAttributePathAttributes) {

						if (attributes.containsKey(outputDataModelSchemaAttribute.getId())) {

							// found candidate for removal

							attributePaths.put(outputDataModelSchemaAttributePath.getId(), outputDataModelSchemaAttributePath);

							// remove candidate from output data model schema
							outputDataModelSchemaAttributePathRemovalCandidates.add(outputDataModelSchemaAttributePath);
						}
					}
				}
			}
		}

		for (final SchemaAttributePathInstance outputDataModelSchemaAttributePath : outputDataModelSchemaAttributePathRemovalCandidates) {

			assert outputDataModelSchema != null;
			outputDataModelSchema.removeAttributePath(outputDataModelSchemaAttributePath);
		}

		// update output data model schema to persist possible changes
		jpaService.updateObjectTransactional(outputDataModelSchema);
	}

	/**
	 * {@inheritDoc}<br/>
	 * Updates the name, attribute paths and record class of the schema.
	 */
	@Override
	protected Schema prepareObjectForUpdate(final Schema objectWithUpdates, final Schema object) {

		super.prepareObjectForUpdate(objectWithUpdates, object);

		final Set<SchemaAttributePathInstance> attributePaths = objectWithUpdates.getUniqueAttributePaths();

		object.setAttributePaths(attributePaths);

		final Clasz recordClass = objectWithUpdates.getRecordClass();

		object.setRecordClass(recordClass);

		return object;
	}

	@Override
	public void reset() {

		attributePathsServiceTestUtils.reset();
		claszesServiceTestUtils.reset();
	}
	
	/**
	 * Creates an exemplary internal schema for bibo:Document with deep paths (not using sub-schemata).
	 * The attribute paths used here do not have meaningful semantics.
	 * Each attribute path is encapsulated by a schema attribute path instance according to the new domain model.
	 * 
	 * @throws Exception
	 */
	public Schema getExampleSchema1() throws Exception {
		
		
		// first attribute path

		final Attribute dctermsTitle = attributesServiceTestUtils.createAttribute("http://purl.org/dc/terms/title", "title");
		final Attribute dctermsHasPart = attributesServiceTestUtils.createAttribute("http://purl.org/dc/terms/hasPart", "hasPart");

		final LinkedList<Attribute> attributePath1Arg = Lists.newLinkedList();

		attributePath1Arg.add(dctermsTitle);
		attributePath1Arg.add(dctermsHasPart);
		attributePath1Arg.add(dctermsTitle);

		System.out.println("attribute title = '" + dctermsTitle.toString());
		System.out.println("attribute hasPart = '" + dctermsHasPart.toString());

		final AttributePath attributePath1 = attributePathsServiceTestUtils.createAttributePath(attributePath1Arg);
		final SchemaAttributePathInstance attributePathInstance1 = 
				schemaAttributePathInstanceServiceTestUtils.createSchemaAttributePathInstance("SAPI-1", attributePath1, null);
		
		// second attribute path

		final String dctermsCreatorId = "http://purl.org/dc/terms/creator";
		final String dctermsCreatorName = "creator";

		final Attribute dctermsCreator = attributesServiceTestUtils.createAttribute(dctermsCreatorId, dctermsCreatorName);

		final String foafNameId = "http://xmlns.com/foaf/0.1/name";
		final String foafNameName = "name";

		final Attribute foafName = attributesServiceTestUtils.createAttribute(foafNameId, foafNameName);

		final LinkedList<Attribute> attributePath2Arg = Lists.newLinkedList();

		attributePath2Arg.add(dctermsCreator);
		attributePath2Arg.add(foafName);

		System.out.println("attribute creator = '" + dctermsCreator.toString());
		System.out.println("attribute name = '" + foafName.toString());

		final AttributePath attributePath2 = attributePathsServiceTestUtils.createAttributePath(attributePath2Arg);
		final SchemaAttributePathInstance attributePathInstance2 = 
				schemaAttributePathInstanceServiceTestUtils.createSchemaAttributePathInstance("SAPI-2", attributePath2, null);
		
		// third attribute path

		final String dctermsCreatedId = "http://purl.org/dc/terms/created";
		final String dctermsCreatedName = "created";

		final Attribute dctermsCreated = attributesServiceTestUtils.createAttribute(dctermsCreatedId, dctermsCreatedName);
		final LinkedList<Attribute> attributePath3Arg = Lists.newLinkedList();

		attributePath3Arg.add(dctermsCreated);

		System.out.println("attribute created = '" + dctermsCreated.toString());

		final AttributePath attributePath3 = attributePathsServiceTestUtils.createAttributePath(attributePath3Arg);
		final SchemaAttributePathInstance attributePathInstance3 = 
				schemaAttributePathInstanceServiceTestUtils.createSchemaAttributePathInstance("SAPI-3", attributePath3, null);
		
		// record class

		final String biboDocumentId = "http://purl.org/ontology/bibo/Document";
		final String biboDocumentName = "document";

		final Clasz biboDocument = claszesServiceTestUtils.createClass(biboDocumentId, biboDocumentName);

		// schema

		final Set<SchemaAttributePathInstance> attributePathInstances = Sets.newLinkedHashSet();

		attributePathInstances.add(attributePathInstance1);
		attributePathInstances.add(attributePathInstance2);
		attributePathInstances.add(attributePathInstance3);

		final Schema schema = createSchema("my schema", attributePathInstances, biboDocument);
		
		return schema;
	}



}
