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
package org.dswarm.persistence.service.schema.test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dswarm.persistence.GuicedTest;
import org.dswarm.persistence.model.schema.Attribute;
import org.dswarm.persistence.model.schema.AttributePath;
import org.dswarm.persistence.model.schema.Clasz;
import org.dswarm.persistence.model.schema.ContentSchema;
import org.dswarm.persistence.model.schema.Schema;
import org.dswarm.persistence.model.schema.SchemaAttributePathInstance;
import org.dswarm.persistence.model.schema.proxy.ProxySchema;
import org.dswarm.persistence.service.schema.SchemaService;
import org.dswarm.persistence.service.schema.test.utils.AttributePathServiceTestUtils;
import org.dswarm.persistence.service.schema.test.utils.AttributeServiceTestUtils;
import org.dswarm.persistence.service.schema.test.utils.ClaszServiceTestUtils;
import org.dswarm.persistence.service.schema.test.utils.ContentSchemaServiceTestUtils;
import org.dswarm.persistence.service.schema.test.utils.SchemaAttributePathInstanceServiceTestUtils;
import org.dswarm.persistence.service.schema.test.utils.SchemaServiceTestUtils;
import org.dswarm.persistence.service.test.IDBasicJPAServiceTest;

public class SchemaServiceTest extends IDBasicJPAServiceTest<ProxySchema, Schema, SchemaService> {

	private static final Logger					LOG				= LoggerFactory.getLogger(SchemaServiceTest.class);

	private ObjectMapper					objectMapper;

	private final Map<Long, Attribute>			attributes		= Maps.newLinkedHashMap();

	private AttributeServiceTestUtils		attributeServiceTestUtils;
	private ClaszServiceTestUtils			claszServiceTestUtils;
	private ContentSchemaServiceTestUtils	contentSchemaServiceTestUtils;
	private AttributePathServiceTestUtils	attributePathServiceTestUtils;
	private SchemaAttributePathInstanceServiceTestUtils	schemaAttributePathInstanceServiceTestUtils;
	private SchemaServiceTestUtils			schemaServiceTestUtils;

	public SchemaServiceTest() {

		super("schema", SchemaService.class);
	
		initObjects();
	}

	@Override
	protected void initObjects() {
	
		super.initObjects();
	
		objectMapper = GuicedTest.injector.getInstance(ObjectMapper.class);
		
		attributeServiceTestUtils = new AttributeServiceTestUtils();
		attributePathServiceTestUtils = new AttributePathServiceTestUtils();
		claszServiceTestUtils = new ClaszServiceTestUtils();
		contentSchemaServiceTestUtils = new ContentSchemaServiceTestUtils();
		schemaAttributePathInstanceServiceTestUtils = new SchemaAttributePathInstanceServiceTestUtils();
		schemaServiceTestUtils = new SchemaServiceTestUtils();
	
	}

	private void resetObjectVars() {
		attributes.clear();
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
	public void testSimpleSchema() throws Exception {

		// first attribute path

		final Attribute dctermsTitle = attributeServiceTestUtils.createAttribute("http://purl.org/dc/terms/title", "title");
		attributes.put(dctermsTitle.getId(), dctermsTitle);

		final Attribute dctermsHasPart = attributeServiceTestUtils.createAttribute("http://purl.org/dc/terms/hasPart", "hasPart");
		attributes.put(dctermsHasPart.getId(), dctermsHasPart);

		final LinkedList<Attribute> attributePath1Arg = Lists.newLinkedList();

		attributePath1Arg.add(dctermsTitle);
		attributePath1Arg.add(dctermsHasPart);
		attributePath1Arg.add(dctermsTitle);

		System.out.println("attribute title = '" + dctermsTitle.toString());
		System.out.println("attribute hasPart = '" + dctermsHasPart.toString());

		final AttributePath attributePath1 = attributePathServiceTestUtils.createAttributePath(attributePath1Arg);

		// second attribute path

		final String dctermsCreatorId = "http://purl.org/dc/terms/creator";
		final String dctermsCreatorName = "creator";

		final Attribute dctermsCreator = attributeServiceTestUtils.createAttribute(dctermsCreatorId, dctermsCreatorName);
		attributes.put(dctermsCreator.getId(), dctermsCreator);

		final String foafNameId = "http://xmlns.com/foaf/0.1/name";
		final String foafNameName = "name";

		final Attribute foafName = attributeServiceTestUtils.createAttribute(foafNameId, foafNameName);
		attributes.put(foafName.getId(), foafName);

		final LinkedList<Attribute> attributePath2Arg = Lists.newLinkedList();

		attributePath2Arg.add(dctermsCreator);
		attributePath2Arg.add(foafName);

		System.out.println("attribute creator = '" + dctermsCreator.toString());
		System.out.println("attribute name = '" + foafName.toString());

		final AttributePath attributePath2 = attributePathServiceTestUtils.createAttributePath(attributePath2Arg);

		// third attribute path

		final String dctermsCreatedId = "http://purl.org/dc/terms/created";
		final String dctermsCreatedName = "created";

		final Attribute dctermsCreated = attributeServiceTestUtils.createAttribute(dctermsCreatedId, dctermsCreatedName);
		attributes.put(dctermsCreated.getId(), dctermsCreated);

		final LinkedList<Attribute> attributePath3Arg = Lists.newLinkedList();

		attributePath3Arg.add(dctermsCreated);

		System.out.println("attribute created = '" + dctermsCreated.toString());

		final AttributePath attributePath3 = attributePathServiceTestUtils.createAttributePath(attributePath3Arg);

		// record class

		final String biboDocumentId = "http://purl.org/ontology/bibo/Document";
		final String biboDocumentName = "document";

		final Clasz biboDocument = claszServiceTestUtils.createClass(biboDocumentId, biboDocumentName);

		// START content schema

		// value attribute path

		final String rdfValueId = "http://www.w3.org/1999/02/22-rdf-syntax-ns#value";
		final String rdfValueName = "value";

		final Attribute rdfValue = attributeServiceTestUtils.createAttribute(rdfValueId, rdfValueName);
		attributes.put(rdfValue.getId(), rdfValue);

		final LinkedList<Attribute> rdfValueAPList = Lists.newLinkedList();
		rdfValueAPList.add(rdfValue);

		final AttributePath rdfValueAP = attributePathServiceTestUtils.createAttributePath(rdfValueAPList);

		// content schema

		final ContentSchema dummyContentSchema = new ContentSchema();

		dummyContentSchema.setName("my content schema");
		dummyContentSchema.addKeyAttributePath(attributePath1);
		dummyContentSchema.addKeyAttributePath(attributePath2);
		dummyContentSchema.addKeyAttributePath(attributePath3);
		dummyContentSchema.setValueAttributePath(rdfValueAP);

		final ContentSchema contentSchema = contentSchemaServiceTestUtils.createContentSchema(dummyContentSchema);

		// END content schema

		// schema

		final Schema schema = createObject().getObject();

		schema.setName("my schema");
		
		SchemaAttributePathInstance attributePathInstance1 = schemaAttributePathInstanceServiceTestUtils.createSchemaAttributePathInstance("api1", attributePath1, null);
		SchemaAttributePathInstance attributePathInstance2 = schemaAttributePathInstanceServiceTestUtils.createSchemaAttributePathInstance("api2", attributePath2, null);
		SchemaAttributePathInstance attributePathInstance3 = schemaAttributePathInstanceServiceTestUtils.createSchemaAttributePathInstance("api3", attributePath3, null);
		
		schema.addAttributePath(attributePathInstance1);
		schema.addAttributePath(attributePathInstance2);
		schema.addAttributePath(attributePathInstance3);
		schema.setRecordClass(biboDocument);
		schema.setContentSchema(contentSchema);

		// update schema

		final Schema updatedSchema = updateObjectTransactional(schema).getObject();
		
		// tests

		Assert.assertNotNull("the schema's attribute paths of the updated schema shouldn't be null",
				updatedSchema.getUniqueAttributePaths());
		
		Assert.assertEquals("the schema's attribute paths are not equal",
				schema.getUniqueAttributePaths(), updatedSchema.getUniqueAttributePaths());
		
		Assert.assertEquals("the attribute path instance '" + attributePathInstance1.getId() + "' of the schema are not equal",
				schema.getAttributePath(attributePathInstance1.getId()), updatedSchema.getAttributePath(attributePathInstance1.getId()));
		
		Assert.assertNotNull("the attributes of attribute path '" + attributePath1.getId()
				+ "' of the updated schema shouldn't be null",
				updatedSchema.getAttributePath(attributePathInstance1.getId()).getAttributePath().getAttributes());
		
		Assert.assertEquals("the attributes of attribute path '" + attributePath1.getId() + "' are not equal",
				attributePath1.getAttributes(),
				updatedSchema.getAttributePath(attributePathInstance1.getId()).getAttributePath().getAttributes());
		
		Assert.assertEquals("the first attributes of attribute path '" + attributePath1.getId() + "' are not equal", attributePath1
				.getAttributePath().get(0),
				updatedSchema.getAttributePath(attributePathInstance1.getId()).getAttributePath().getAttributePath().get(0));
		
		Assert.assertNotNull("the attribute path string of attribute path '" + attributePath1.getId() + "' of the update schema shouldn't be null",
				updatedSchema.getAttributePath(attributePathInstance1.getId()).getAttributePath().toAttributePath());
		
		Assert.assertEquals("the attribute path's strings attribute path '" + attributePath1.getId() + "' are not equal",
				attributePath1.toAttributePath(), updatedSchema.getAttributePath(attributePathInstance1.getId()).getAttributePath().toAttributePath());
		
		Assert.assertNotNull("the record class of the updated schema shouldn't be null", updatedSchema.getRecordClass());
		Assert.assertEquals("the recod classes are not equal", schema.getRecordClass(), updatedSchema.getRecordClass());
		
		Assert.assertNotNull("the content schema of the updated schema shouldn't be null", updatedSchema.getContentSchema());
		Assert.assertEquals("the content schemata are not equal", schema.getContentSchema(), updatedSchema.getContentSchema());

		// json mapping
		
		String json = null;

		try {

			json = objectMapper.writeValueAsString(schema);
			
		} catch (final JsonProcessingException e) {

			e.printStackTrace();
		}

		SchemaServiceTest.LOG.debug("schema json: " + json);

//		// clean up DB
//		deleteObject(schema.getId());
//
//		claszServiceTestUtils.deleteObject(biboDocument);
//		contentSchemaServiceTestUtils.deleteObject(contentSchema);
//
//		attributePathServiceTestUtils.deleteObject(attributePath1);
//		attributePathServiceTestUtils.deleteObject(attributePath2);
//		attributePathServiceTestUtils.deleteObject(attributePath3);
//		attributePathServiceTestUtils.deleteObject(rdfValueAP);
//
//		for (final Attribute attribute : attributes.values()) {
//
//			attributeServiceTestUtils.deleteObject(attribute);
//		}
	}

	
	@Test
	public void testComplexSchema() throws Exception {
		
		Schema schema = createDocumentSchema();
		
		String json = null;

		try {
			json = objectMapper.writeValueAsString(schema);
		} catch (final JsonProcessingException e) {
			e.printStackTrace();
		}

		SchemaServiceTest.LOG.debug("schema json: " + json);
	}
	
	private Schema createPersonSchema() throws Exception {
		
		final Attribute foafLastName = attributeServiceTestUtils.createAttribute("http://xmlns.com/foaf/0.1/firstname", "first name");
		final Attribute foafFirstName = attributeServiceTestUtils.createAttribute("http://xmlns.com/foaf/0.1/lastname", "last name");
		
		final SchemaAttributePathInstance attributePathInstance1 = schemaAttributePathInstanceServiceTestUtils.createSchemaAttributePathInstance(foafFirstName);
		final SchemaAttributePathInstance attributePathInstance2 = schemaAttributePathInstanceServiceTestUtils.createSchemaAttributePathInstance(foafLastName);
		
		final Clasz foafPerson = claszServiceTestUtils.createClass("http://xmlns.com/foaf/0.1/Person", "Person");
		
		Set<SchemaAttributePathInstance> attributePaths = new HashSet<SchemaAttributePathInstance>();
		attributePaths.add(attributePathInstance1);
		attributePaths.add(attributePathInstance2);
		
		Schema personSchema = schemaServiceTestUtils.createSchema("Person schema", attributePaths, foafPerson);

		return personSchema;
	}
	
	private Schema createDocumentSchema() throws Exception {
		
		final Attribute dctermsTitle = attributeServiceTestUtils.createAttribute("http://purl.org/dc/terms/title", "title");
		final Attribute dctermsHasPart = attributeServiceTestUtils.createAttribute("http://purl.org/dc/terms/hasPart", "hasPart");
		final Attribute dctermsCreator = attributeServiceTestUtils.createAttribute("http://purl.org/dc/terms/creator", "creator");
		
		final Schema personSchema = createPersonSchema();
		
		final SchemaAttributePathInstance attributePathInstance1 = schemaAttributePathInstanceServiceTestUtils.
				createSchemaAttributePathInstance(dctermsTitle);
		final SchemaAttributePathInstance attributePathInstance2 = schemaAttributePathInstanceServiceTestUtils.
				createSchemaAttributePathInstance(dctermsHasPart);
		final SchemaAttributePathInstance attributePathInstance3 = schemaAttributePathInstanceServiceTestUtils.
				createSchemaAttributePathInstance(dctermsCreator, personSchema);
		
		final Clasz biboDocument = claszServiceTestUtils.createClass("http://purl.org/ontology/bibo/Document", "Document");
		
		Set<SchemaAttributePathInstance> attributePaths = new HashSet<SchemaAttributePathInstance>();
		attributePaths.add(attributePathInstance1);
		attributePaths.add(attributePathInstance2);
		attributePaths.add(attributePathInstance3);

		Schema documentSchema = schemaServiceTestUtils.createSchema("Document schema", attributePaths, biboDocument);
		
		// minimum testing here, some more possible, but other functionality also already covered by other tests in this test class
		Schema personSchemaAsSubSchema = documentSchema.getAttributePath(attributePathInstance3.getId()).getSubSchema();
		Assert.assertNotNull("The subschema got lost.", personSchemaAsSubSchema);
		Assert.assertEquals("The subschema is not the expected one.", personSchema, personSchemaAsSubSchema);
		Assert.assertNotNull("The schema's subschema has no attribute paths (null).", personSchemaAsSubSchema.getAttributePaths());
		Assert.assertFalse("The schema's subschema has no attribute paths (empty).", personSchemaAsSubSchema.getAttributePaths().isEmpty());
		
		return documentSchema;
	}

}
