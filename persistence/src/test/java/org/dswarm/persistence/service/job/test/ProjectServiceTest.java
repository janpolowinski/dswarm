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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dswarm.persistence.DMPPersistenceException;
import org.dswarm.persistence.GuicedTest;
import org.dswarm.persistence.model.job.Component;
import org.dswarm.persistence.model.job.Function;
import org.dswarm.persistence.model.job.FunctionType;
import org.dswarm.persistence.model.job.Mapping;
import org.dswarm.persistence.model.job.Project;
import org.dswarm.persistence.model.job.Transformation;
import org.dswarm.persistence.model.job.proxy.ProxyProject;
import org.dswarm.persistence.model.resource.Configuration;
import org.dswarm.persistence.model.resource.DataModel;
import org.dswarm.persistence.model.resource.Resource;
import org.dswarm.persistence.model.resource.ResourceType;
import org.dswarm.persistence.model.schema.Attribute;
import org.dswarm.persistence.model.schema.AttributePath;
import org.dswarm.persistence.model.schema.Clasz;
import org.dswarm.persistence.model.schema.MappingAttributePathInstance;
import org.dswarm.persistence.model.schema.Schema;
import org.dswarm.persistence.model.schema.SchemaAttributePathInstance;
import org.dswarm.persistence.service.job.MappingService;
import org.dswarm.persistence.service.job.ProjectService;
import org.dswarm.persistence.service.job.test.utils.ComponentServiceTestUtils;
import org.dswarm.persistence.service.job.test.utils.FunctionServiceTestUtils;
import org.dswarm.persistence.service.job.test.utils.TransformationServiceTestUtils;
import org.dswarm.persistence.service.resource.DataModelService;
import org.dswarm.persistence.service.resource.test.utils.ConfigurationServiceTestUtils;
import org.dswarm.persistence.service.resource.test.utils.ResourceServiceTestUtils;
import org.dswarm.persistence.service.schema.test.utils.AttributePathServiceTestUtils;
import org.dswarm.persistence.service.schema.test.utils.AttributeServiceTestUtils;
import org.dswarm.persistence.service.schema.test.utils.ClaszServiceTestUtils;
import org.dswarm.persistence.service.schema.test.utils.MappingAttributePathInstanceServiceTestUtils;
import org.dswarm.persistence.service.schema.test.utils.SchemaAttributePathInstanceServiceTestUtils;
import org.dswarm.persistence.service.schema.test.utils.SchemaServiceTestUtils;
import org.dswarm.persistence.service.test.IDBasicJPAServiceTest;
import org.dswarm.persistence.util.DMPPersistenceUtil;

public class ProjectServiceTest extends IDBasicJPAServiceTest<ProxyProject, Project, ProjectService> {

	private static final Logger									LOG								= LoggerFactory.getLogger(ProjectServiceTest.class);

	private ObjectMapper										objectMapper;

	private final Map<Long, Function>							functions						= Maps.newLinkedHashMap();

	private final Map<Long, Attribute>							attributes						= Maps.newLinkedHashMap();

	private final Map<Long, Clasz>								classes							= Maps.newLinkedHashMap();

	private final Map<Long, AttributePath>						attributePaths					= Maps.newLinkedHashMap();
	
	private final Map<Long, SchemaAttributePathInstance>		schemaAttributePathInstances	= Maps.newLinkedHashMap();

	private final Map<Long, Component>							components						= Maps.newLinkedHashMap();

	private final Map<Long, Transformation>						transformations					= Maps.newLinkedHashMap();

	private final Map<Long, Mapping>							mappings						= Maps.newLinkedHashMap();

	private final Map<Long, Schema>								schemas							= Maps.newLinkedHashMap();

	private final Map<Long, Resource>							resources						= Maps.newLinkedHashMap();

	private final Map<Long, Configuration>						configurations					= Maps.newLinkedHashMap();

	private final Map<Long, MappingAttributePathInstance>		mappingAttributePathInstances	= Maps.newLinkedHashMap();

	private AttributeServiceTestUtils						attributeServiceTestUtils;
	private AttributePathServiceTestUtils					attributePathServiceTestUtils;
	private SchemaAttributePathInstanceServiceTestUtils		schemaAttributePathInstanceServiceTestUtils;
	private FunctionServiceTestUtils						functionServiceTestUtils;
	private MappingAttributePathInstanceServiceTestUtils	mappingAttributePathInstanceServiceTestUtils;
	private ComponentServiceTestUtils						componentServiceTestUtils;
	private TransformationServiceTestUtils					transformationServiceTestUtils;
	private SchemaServiceTestUtils							schemaServiceTestUtils;
	private ConfigurationServiceTestUtils					configurationServiceTestUtils;
	private ResourceServiceTestUtils						resourceServiceTestUtils;
	private ClaszServiceTestUtils							claszServiceTestUtils;
//	private MappingServiceTestUtils							mappingServiceTestUtils;
//	private DataModelServiceTestUtils						dataModelServiceTestUtils;

	public ProjectServiceTest() {

		super("project", ProjectService.class);

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
		schemaAttributePathInstanceServiceTestUtils = new SchemaAttributePathInstanceServiceTestUtils();
		componentServiceTestUtils = new ComponentServiceTestUtils();
		transformationServiceTestUtils = new TransformationServiceTestUtils();
		claszServiceTestUtils = new ClaszServiceTestUtils();
		schemaServiceTestUtils = new SchemaServiceTestUtils();
		configurationServiceTestUtils = new ConfigurationServiceTestUtils();
		resourceServiceTestUtils = new ResourceServiceTestUtils();
//		mappingServiceTestUtils = new MappingServiceTestUtils();
//		dataModelServiceTestUtils = new DataModelServiceTestUtils();

	}
	
	private void resetObjectVars() {
		
		functions.clear();
		attributes.clear();
		classes.clear();
		attributePaths.clear();
		schemaAttributePathInstances.clear();
		components.clear();
		transformations.clear();
		mappings.clear();
		schemas.clear();
		resources.clear();
		configurations.clear();
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
	public void simpleProjectTest() throws Exception {

		ProjectServiceTest.LOG.debug("start simple project test");

		// mappings

		final Mapping simpleMapping = createMapping();
		final Mapping complexMapping = createComplexMapping();

		final Set<Mapping> mappings = Sets.newLinkedHashSet();
		mappings.add(simpleMapping);
		mappings.add(complexMapping);

		// data models

		final DataModel inputDataModel = createInputDataModel();

		final DataModel outputDataModel = createOutputDataModel();

		// functions

		final LinkedList<String> parameters = Lists.newLinkedList();

		parameters.add("inputString");

		final Function function1 = functionServiceTestUtils.createFunction("trim", "trims leading and trailing whitespaces from a given string",
				parameters);
		functions.put(function1.getId(), function1);

		final Set<Function> functions = Sets.newLinkedHashSet();
		functions.add(function1);

		// project

		final String projectName = "my project";
		final String projectDescription = "my project description";

		final Project project = createObject().getObject();

		project.setName(projectName);
		project.setDescription(projectDescription);
		project.setInputDataModel(inputDataModel);
		project.setOutputDataModel(outputDataModel);
		project.setMappings(mappings);
		project.setFunctions(functions);

		final Project updatedProject = updateObjectTransactional(project).getObject();

		assertNotNull("the update project shouldn't be null", updatedProject);
		assertNotNull("the id of the updated project shouldn't be null", updatedProject.getId());
		assertEquals("the id of the updated project isn't the same as of project", project.getId(), updatedProject.getId());
		assertNotNull("the name of the updated project shouldn't be null", updatedProject.getName());
		assertEquals("the name of the updated project isn't the same as of project", project.getName(), updatedProject.getName());
		assertNotNull("the description of the updated project shouldn't be null", updatedProject.getDescription());
		assertEquals("the description of the updated project isn't the same as of project", project.getDescription(),
				updatedProject.getDescription());

		// input data model

		assertNotNull("the input data model of the updated project shouldn't be null", updatedProject.getInputDataModel());
		assertEquals("the input data model of the updated project isn't the same as of project", project.getInputDataModel(),
				updatedProject.getInputDataModel());
		assertNotNull("the input data model id of the updated project shouldn't be null", updatedProject.getInputDataModel().getId());
		assertEquals("the input data model id of the updated project isn't the same as of project", project.getInputDataModel().getId(),
				updatedProject.getInputDataModel().getId());
		assertNotNull("the input data model name of the updated project shouldn't be null", updatedProject.getInputDataModel().getName());
		assertEquals("the input data model name of the updated project isn't the same as of project", project.getInputDataModel().getName(),
				updatedProject.getInputDataModel().getName());
		assertNotNull("the input data model data resource of the updated project shouldn't be null", updatedProject.getInputDataModel()
				.getDataResource());
		assertEquals("the input data model data resource of the updated project isn't the same as of project", project.getInputDataModel()
				.getDataResource(), updatedProject.getInputDataModel().getDataResource());
		assertNotNull("the input data model data resource id of the updated project shouldn't be null", updatedProject.getInputDataModel()
				.getDataResource().getId());
		assertEquals("the input data model data resource id of the updated project isn't the same as of project", project.getInputDataModel()
				.getDataResource().getId(), updatedProject.getInputDataModel().getDataResource().getId());
		assertNotNull("the input data model data resource name of the updated project shouldn't be null", updatedProject.getInputDataModel()
				.getDataResource().getName());
		assertEquals("the input data model data resource name of the updated project isn't the same as of project", project
				.getInputDataModel().getDataResource().getName(), updatedProject.getInputDataModel().getDataResource().getName());
		assertNotNull("the input data model configuration of the updated project shouldn't be null", updatedProject.getInputDataModel()
				.getConfiguration());
		assertEquals("the input data model configuration of the updated project isn't the same as of project", project.getInputDataModel()
				.getConfiguration(), updatedProject.getInputDataModel().getConfiguration());
		assertNotNull("the input data model configuration id of the updated project shouldn't be null", updatedProject.getInputDataModel()
				.getConfiguration().getId());
		assertEquals("the input data model configuration id of the updated project isn't the same as of project", project.getInputDataModel()
				.getConfiguration().getId(), updatedProject.getInputDataModel().getConfiguration().getId());
		assertNotNull("the input data model configuration name of the updated project shouldn't be null", updatedProject.getInputDataModel()
				.getConfiguration().getName());
		assertEquals("the input data model configuration name of the updated project isn't the same as of project", project
				.getInputDataModel().getConfiguration().getName(), updatedProject.getInputDataModel().getConfiguration().getName());
		assertNotNull("the input data model schema of the updated project shouldn't be null", updatedProject.getInputDataModel().getSchema());
		assertEquals("the input data model schema of the updated project isn't the same as of project", project.getInputDataModel()
				.getSchema(), updatedProject.getInputDataModel().getSchema());
		assertNotNull("the input data model schema id of the updated project shouldn't be null", updatedProject.getInputDataModel()
				.getSchema().getId());
		assertEquals("the input data model schema id of the updated project isn't the same as of project", project.getInputDataModel()
				.getSchema().getId(), updatedProject.getInputDataModel().getSchema().getId());
		assertNotNull("the input data model schema name of the updated project shouldn't be null", updatedProject.getInputDataModel()
				.getSchema().getName());
		assertEquals("the input data model schema name of the updated project isn't the same as of project", project.getInputDataModel()
				.getSchema().getName(), updatedProject.getInputDataModel().getSchema().getName());

		// output data model

		assertNotNull("the output data model of the updated project shouldn't be null", updatedProject.getOutputDataModel());
		assertEquals("the output data model of the updated project isn't the same as of project", project.getOutputDataModel(),
				updatedProject.getOutputDataModel());
		assertNotNull("the output data model id of the updated project shouldn't be null", updatedProject.getOutputDataModel().getId());
		assertEquals("the output data model id of the updated project isn't the same as of project", project.getOutputDataModel().getId(),
				updatedProject.getOutputDataModel().getId());
		assertNotNull("the output data model name of the updated project shouldn't be null", updatedProject.getOutputDataModel().getName());
		assertEquals("the output data model name of the updated project isn't the same as of project", project.getOutputDataModel().getName(),
				updatedProject.getOutputDataModel().getName());
		assertNotNull("the output data model schema of the updated project shouldn't be null", updatedProject.getOutputDataModel().getSchema());
		assertEquals("the output data model schema of the updated project isn't the same as of project", project.getOutputDataModel()
				.getSchema(), updatedProject.getOutputDataModel().getSchema());
		assertNotNull("the output data model schema id of the updated project shouldn't be null", updatedProject.getOutputDataModel()
				.getSchema().getId());
		assertEquals("the output data model schema id of the updated project isn't the same as of project", project.getOutputDataModel()
				.getSchema().getId(), updatedProject.getOutputDataModel().getSchema().getId());
		assertNotNull("the output data model schema name of the updated project shouldn't be null", updatedProject.getOutputDataModel()
				.getSchema().getName());
		assertEquals("the output data model schema name of the updated project isn't the same as of project", project.getOutputDataModel()
				.getSchema().getName(), updatedProject.getOutputDataModel().getSchema().getName());

		// project mappings

		assertNotNull("the mappings of the updated project shouldn't be null", updatedProject.getMappings());
		assertEquals("the size of the mappings collection of the updated project isn't the same as of project", project.getMappings().size(),
				updatedProject.getMappings().size());
		assertNotNull("the mapping of the updated project shouldn't be null", updatedProject.getMappings().iterator().next());

		final Iterator<Mapping> mappingIter = updatedProject.getMappings().iterator();

		Mapping updatedComplexMapping = null;

		while (mappingIter.hasNext()) {

			final Mapping iterMapping = mappingIter.next();

			if (complexMapping.getId().equals(iterMapping.getId())) {

				updatedComplexMapping = iterMapping;

				break;
			}
		}

		assertNotNull("the updated complex mapping of the updated project shouldn't be null", updatedComplexMapping);
		assertNotNull("the mapping name shouldn't be null", updatedComplexMapping.getName());
		assertEquals("the mapping names are not equal", complexMapping.getName(), updatedComplexMapping.getName());
		assertNotNull("the mapping transformation component shouldn't be null", updatedComplexMapping.getTransformation());
		assertEquals("the mapping transformation components are not equal", complexMapping.getTransformation(),
				updatedComplexMapping.getTransformation());
		assertNotNull("the transformation component id shouldn't be null", updatedComplexMapping.getTransformation().getId());
		assertEquals("the mapping transformation components' ids are not equal", complexMapping.getTransformation().getId(),
				updatedComplexMapping.getTransformation().getId());
		assertNotNull("the transformation component name shouldn't be null", updatedComplexMapping.getTransformation().getName());
		assertEquals("the mapping transformation components' names are not equal", complexMapping.getTransformation().getName(),
				updatedComplexMapping.getTransformation().getName());
		assertNotNull("the transformation component function shouldn't be null", updatedComplexMapping.getTransformation().getFunction());
		assertEquals("the mapping transformation components' functions are not equal", complexMapping.getTransformation().getFunction(),
				updatedComplexMapping.getTransformation().getFunction());
		assertNotNull("the transformation component function id shouldn't be null", updatedComplexMapping.getTransformation().getFunction()
				.getId());
		assertEquals("the mapping transformation components' functions' ids are not equal", complexMapping.getTransformation().getFunction()
				.getId(), updatedComplexMapping.getTransformation().getFunction().getId());
		assertNotNull("the transformation component function name shouldn't be null", updatedComplexMapping.getTransformation().getFunction()
				.getName());
		assertEquals("the mapping transformation components' functions' names are not equal", complexMapping.getTransformation().getFunction()
				.getName(), updatedComplexMapping.getTransformation().getFunction().getName());

		// project functions

		assertNotNull("the functions of the updated project shouldn't be null", updatedProject.getFunctions());
		assertEquals("the size of the functions collection of the updated project isn't the same as of project",
				project.getFunctions().size(), updatedProject.getFunctions().size());
		assertNotNull("the function of the updated project shouldn't be null", updatedProject.getFunctions().iterator().next());
		assertEquals("the function of the updated project isn't the same as of project", function1, updatedProject.getFunctions().iterator()
				.next());
		assertEquals("the function id of the updated project isn't the same as of project", function1.getId(), updatedProject.getFunctions()
				.iterator().next().getId());
		assertEquals("the function name of the updated project isn't the same as of project", function1.getName(), updatedProject
				.getFunctions().iterator().next().getName());

		String json = null;

		try {

			json = objectMapper.writeValueAsString(updatedProject);
		} catch (final JsonProcessingException e) {

			e.printStackTrace();
		}

		ProjectServiceTest.LOG.debug("project json: " + json);

		ProjectServiceTest.LOG.debug("end simple project test");
	}


	private Mapping createMapping() throws Exception {

		final MappingService mappingService = GuicedTest.injector.getInstance(MappingService.class);

		// function

		final LinkedList<String> parameters = Lists.newLinkedList();

		parameters.add("inputString");

		final Function function = functionServiceTestUtils.createFunction("trim", "trims leading and trailing whitespaces from a given string",
				parameters);
		functions.put(function.getId(), function);

		final String componentName = "my trim component";
		final Map<String, String> parameterMappings = Maps.newLinkedHashMap();

		final String functionParameterName = "inputString";
		final String componentVariableName = "previousComponent.outputString";

		parameterMappings.put(functionParameterName, componentVariableName);

		final Component component = componentServiceTestUtils.createComponent(componentName, parameterMappings, function, null, null);
		components.put(component.getId(), component);

		// transformation

		final String transformationName = "my transformation";
		final String transformationDescription = "transformation which just makes use of one function";
		final String transformationParameter = "transformationInputString";

		final LinkedList<String> transformationParameters = Lists.newLinkedList();
		transformationParameters.add(transformationParameter);

		final Set<Component> components = Sets.newLinkedHashSet();

		components.add(component);

		final Transformation transformation = transformationServiceTestUtils.createTransformation(transformationName, transformationDescription,
				components, transformationParameters);
		transformations.put(transformation.getId(), transformation);

		// attribute paths

		// input attribute path

		final String dctermsTitleId = "http://purl.org/dc/terms/title";
		final String dctermsTitleName = "title";

		final Attribute dctermsTitle = attributeServiceTestUtils.createAttribute(dctermsTitleId, dctermsTitleName);
		attributes.put(dctermsTitle.getId(), dctermsTitle);

		final LinkedList<Attribute> dctermsTitleAttributePath = Lists.newLinkedList();
		dctermsTitleAttributePath.add(dctermsTitle);

		final AttributePath inputAttributePath = attributePathServiceTestUtils.createAttributePath(dctermsTitleAttributePath);
		attributePaths.put(inputAttributePath.getId(), inputAttributePath);

		// input mapping attribute path instance

		final MappingAttributePathInstance inputMappingAttributePathInstance = mappingAttributePathInstanceServiceTestUtils
				.createMappingAttributePathInstance("input mapping attribute path instance", inputAttributePath, null, null);
		mappingAttributePathInstances.put(inputMappingAttributePathInstance.getId(), inputMappingAttributePathInstance);

		// output attribute path

		final String rdfsLabelId = "http://www.w3.org/2000/01/rdf-schema#label";
		final String rdfsLabelName = "label";

		final Attribute rdfsLabel = attributeServiceTestUtils.createAttribute(rdfsLabelId, rdfsLabelName);
		attributes.put(rdfsLabel.getId(), rdfsLabel);

		final LinkedList<Attribute> rdfsLabelAttributePath = Lists.newLinkedList();
		rdfsLabelAttributePath.add(rdfsLabel);

		final AttributePath outputAttributePath = attributePathServiceTestUtils.createAttributePath(rdfsLabelAttributePath);
		attributePaths.put(outputAttributePath.getId(), outputAttributePath);

		// output mapping attribute path instance

		final MappingAttributePathInstance outputMappingAttributePathInstance = mappingAttributePathInstanceServiceTestUtils
				.createMappingAttributePathInstance("output mapping attribute path instance", outputAttributePath, null, null);
		mappingAttributePathInstances.put(outputMappingAttributePathInstance.getId(), outputMappingAttributePathInstance);

		// transformation component

		final Map<String, String> transformationComponentParameterMappings = Maps.newLinkedHashMap();

		transformationComponentParameterMappings.put(transformation.getParameters().get(0), inputAttributePath.toAttributePath());
		transformationComponentParameterMappings.put("transformationOutputVariable", outputAttributePath.toAttributePath());

		final Component transformationComponent = componentServiceTestUtils.createComponent(transformation.getName() + " (component)",
				transformationComponentParameterMappings, transformation, null, null);
		this.components.put(transformationComponent.getId(), transformationComponent);

		// mapping

		final String mappingName = "my mapping";

		Mapping mapping = null;

		try {

			mapping = mappingService.createObjectTransactional().getObject();
		} catch (final DMPPersistenceException e) {

			assertTrue("something went wrong while mapping creation.\n" + e.getMessage(), false);
		}

		mapping.setName(mappingName);
		mapping.addInputAttributePath(inputMappingAttributePathInstance);
		mapping.setOutputAttributePath(outputMappingAttributePathInstance);
		mapping.setTransformation(transformationComponent);

		Mapping updatedMapping = null;

		try {

			updatedMapping = mappingService.updateObjectTransactional(mapping).getObject();
		} catch (final DMPPersistenceException e) {

			assertTrue("something went wrong while updating the mapping of id = '" + mapping.getId() + "'", false);
		}

		assertNotNull("the mapping shouldn't be null", updatedMapping);
		assertNotNull("the mapping id shouldn't be null", updatedMapping.getId());
		assertNotNull("the mapping name shouldn't be null", updatedMapping.getName());
		assertEquals("the mapping names are not equal", mappingName, updatedMapping.getName());
		assertNotNull("the transformation component id shouldn't be null", updatedMapping.getTransformation().getId());
		assertEquals("the transformation component ids are not equal", transformationComponent.getId(), updatedMapping.getTransformation()
				.getId());
		assertNotNull("the transformation component parameter mappings shouldn't be null", updatedMapping.getTransformation()
				.getParameterMappings());
		assertEquals("the transformation component parameter mappings' size are not equal", 2, updatedMapping.getTransformation()
				.getParameterMappings().size());
		assertTrue("the transformation component parameter mappings doesn't contain a mapping for function parameter '"
				+ transformation.getParameters().get(0) + "'",
				updatedMapping.getTransformation().getParameterMappings().containsKey(transformation.getParameters().get(0)));
		assertEquals("the transformation component parameter mapping for '" + transformation.getParameters().get(0) + "' are not equal",
				inputAttributePath.toAttributePath(),
				updatedMapping.getTransformation().getParameterMappings().get(transformation.getParameters().get(0)));
		assertNotNull("the transformation shouldn't be null", updatedMapping.getTransformation().getFunction());
		assertNotNull("the transformation id shouldn't be null", updatedMapping.getTransformation().getFunction().getId());
		assertEquals("the transformation ids are not equal", transformation.getId(), updatedMapping.getTransformation().getFunction().getId());
		assertNotNull("the transformation name shouldn't be null", updatedMapping.getTransformation().getFunction().getName());
		assertEquals("the transformation names are not equal", transformationName, updatedMapping.getTransformation().getFunction().getName());
		assertNotNull("the transformation description shouldn't be null", updatedMapping.getTransformation().getFunction().getDescription());
		assertEquals("the transformation descriptions are not equal", transformationDescription, updatedMapping.getTransformation()
				.getFunction().getDescription());
		assertEquals("the transformation parameters' size are not equal", 1, updatedMapping.getTransformation().getFunction().getParameters()
				.size());
		assertTrue("the transformation parameters doesn't contain transformation parameter '" + transformationParameter + "'", updatedMapping
				.getTransformation().getFunction().getParameters().contains(transformationParameter));
		assertEquals("the transformation parameter for '" + transformationParameter + "' are not equal", transformationParameter,
				updatedMapping.getTransformation().getFunction().getParameters().iterator().next());
		assertNotNull("the transformation components set shouldn't be null", ((Transformation) updatedMapping.getTransformation()
				.getFunction()).getComponents());
		assertEquals("the transformation component sets are not equal", components, ((Transformation) updatedMapping.getTransformation()
				.getFunction()).getComponents());
		assertNotNull("the component id shouldn't be null", ((Transformation) updatedMapping.getTransformation().getFunction())
				.getComponents().iterator().next().getId());
		assertEquals("the component ids are not equal", component.getId(), ((Transformation) updatedMapping.getTransformation().getFunction())
				.getComponents().iterator().next().getId());
		assertNotNull("the component name shouldn't be null", ((Transformation) updatedMapping.getTransformation().getFunction())
				.getComponents().iterator().next().getName());
		assertEquals("the component names are not equal", componentName, ((Transformation) updatedMapping.getTransformation().getFunction())
				.getComponents().iterator().next().getName());
		assertNotNull("the component parameter mappings shouldn't be null",
				((Transformation) updatedMapping.getTransformation().getFunction()).getComponents().iterator().next().getParameterMappings());
		assertEquals("the component parameter mappings' size are not equal", 1, ((Transformation) updatedMapping.getTransformation()
				.getFunction()).getComponents().iterator().next().getParameterMappings().size());
		assertTrue("the component parameter mappings doesn't contain a mapping for function parameter '" + functionParameterName + "'",
				((Transformation) updatedMapping.getTransformation().getFunction()).getComponents().iterator().next().getParameterMappings()
						.containsKey(functionParameterName));
		assertEquals(
				"the component parameter mapping for '" + functionParameterName + "' are not equal",
				componentVariableName,
				((Transformation) updatedMapping.getTransformation().getFunction()).getComponents().iterator().next().getParameterMappings()
						.get(functionParameterName));

		String json = null;

		try {

			json = objectMapper.writeValueAsString(updatedMapping);
		} catch (final JsonProcessingException e) {

			e.printStackTrace();
		}

		ProjectServiceTest.LOG.debug("mapping json: " + json);

		mappings.put(updatedMapping.getId(), updatedMapping);

		return updatedMapping;
	}

	private Mapping createComplexMapping() throws Exception {

		final MappingService mappingService = GuicedTest.injector.getInstance(MappingService.class);

		// previous component

		final String function1Name = "replace";
		final String function1Description = "replace certain parts of a given string that matches a certain regex";
		final String function1Parameter = "inputString";
		final String function2Parameter = "regex";
		final String function3Parameter = "replaceString";

		final LinkedList<String> function1Parameters = Lists.newLinkedList();
		function1Parameters.add(function1Parameter);
		function1Parameters.add(function2Parameter);
		function1Parameters.add(function3Parameter);

		final Function function1 = functionServiceTestUtils.createFunction(function1Name, function1Description, function1Parameters);
		functions.put(function1.getId(), function1);

		final String component1Name = "my replace component";
		final Map<String, String> parameterMapping1 = Maps.newLinkedHashMap();

		final String functionParameterName1 = "inputString";
		final String componentVariableName1 = "previousComponent.outputString";
		final String functionParameterName2 = "regex";
		final String componentVariableName2 = "\\.";
		final String functionParameterName3 = "replaceString";
		final String componentVariableName3 = ":";

		parameterMapping1.put(functionParameterName1, componentVariableName1);
		parameterMapping1.put(functionParameterName2, componentVariableName2);
		parameterMapping1.put(functionParameterName3, componentVariableName3);

		final Component component1 = componentServiceTestUtils.createComponent(component1Name, parameterMapping1, function1, null, null);
		components.put(component1.getId(), component1);

		// next component

		final String function2Name = "lower_case";
		final String function2Description = "lower cases all characters of a given string";
		final String function4Parameter = "inputString";

		final LinkedList<String> function2Parameters = Lists.newLinkedList();
		function2Parameters.add(function4Parameter);

		final Function function2 = functionServiceTestUtils.createFunction(function2Name, function2Description, function2Parameters);
		functions.put(function2.getId(), function2);

		final String component2Name = "my lower case component";
		final Map<String, String> parameterMapping2 = Maps.newLinkedHashMap();

		final String functionParameterName4 = "inputString";
		final String componentVariableName4 = "previousComponent.outputString";

		parameterMapping2.put(functionParameterName4, componentVariableName4);

		final Component component2 = componentServiceTestUtils.createComponent(component2Name, parameterMapping2, function2, null, null);
		components.put(component2.getId(), component2);

		// main component

		final String functionName = "trim";
		final String functionDescription = "trims leading and trailing whitespaces from a given string";
		final String functionParameter = "inputString";

		final LinkedList<String> functionParameters = Lists.newLinkedList();
		functionParameters.add(functionParameter);

		final Function function = functionServiceTestUtils.createFunction(functionName, functionDescription, functionParameters);
		functions.put(function.getId(), function);

		// final String componentId = UUID.randomUUID().toString();
		final String componentName = "my trim component";
		final Map<String, String> parameterMapping = Maps.newLinkedHashMap();

		final String functionParameterName = "inputString";
		final String componentVariableName = "previousComponent.outputString";

		parameterMapping.put(functionParameterName, componentVariableName);

		final Set<Component> inputComponents = Sets.newLinkedHashSet();

		inputComponents.add(component1);

		final Set<Component> outputComponents = Sets.newLinkedHashSet();

		outputComponents.add(component2);

		final Component component = componentServiceTestUtils.createComponent(componentName, parameterMapping, function, inputComponents,
				outputComponents);
		components.put(component.getId(), component);

		// transformation

		final String transformationName = "my transformation";
		final String transformationDescription = "transformation which makes use of three functions";
		final String transformationParameter = "transformationInputString";

		final Set<Component> components = Sets.newLinkedHashSet();

		components.add(component.getInputComponents().iterator().next());
		components.add(component);
		components.add(component.getOutputComponents().iterator().next());

		final LinkedList<String> transformationParameters = Lists.newLinkedList();

		transformationParameters.add(transformationParameter);

		final Transformation transformation = transformationServiceTestUtils.createTransformation(transformationName, transformationDescription,
				components, transformationParameters);
		transformations.put(transformation.getId(), transformation);

		assertNotNull("the transformation components set shouldn't be null", transformation.getComponents());
		assertEquals("the transformation components sizes are not equal", 3, transformation.getComponents().size());

		// transformation component 1 (in main transformation) -> clean first name

		final String transformationComponentFunctionParameterName = "transformationInputString";
		final String transformationComponentVariableName = "firstName";

		final Map<String, String> transformationComponentParameterMappings = Maps.newLinkedHashMap();

		transformationComponentParameterMappings.put(transformationComponentFunctionParameterName, transformationComponentVariableName);

		final String transformationComponentName = "prepare first name";

		final Component transformationComponent = componentServiceTestUtils.createComponent(transformationComponentName,
				transformationComponentParameterMappings, transformation, null, null);
		this.components.put(transformationComponent.getId(), transformationComponent);

		// transformation component 2 (in main transformation) -> clean family name

		final Map<String, String> transformationComponentParameterMappings2 = Maps.newLinkedHashMap();

		transformationComponentParameterMappings2.put("transformationInputString", "familyName");

		final Component transformationComponent2 = componentServiceTestUtils.createComponent("prepare family name",
				transformationComponentParameterMappings2, transformation, null, null);
		this.components.put(transformationComponent2.getId(), transformationComponent2);

		// concat component -> full name

		final String function4Name = "concat";
		final String function4Description = "concatenates two given string";
		final String function5Parameter = "firstString";
		final String function6Parameter = "secondString";

		final LinkedList<String> function4Parameters = Lists.newLinkedList();
		function4Parameters.add(function5Parameter);
		function4Parameters.add(function6Parameter);

		final Function function4 = functionServiceTestUtils.createFunction(function4Name, function4Description, function4Parameters);
		functions.put(function4.getId(), function4);

		final String component4Name = "full name";
		final Map<String, String> parameterMapping4 = Maps.newLinkedHashMap();

		final String functionParameterName5 = "firstString";
		final String componentVariableName5 = transformationComponent.getId() + ".outputVariable";
		final String functionParameterName6 = "secondString";
		final String componentVariableName6 = transformationComponent2.getId() + ".outputVariable";

		parameterMapping4.put(functionParameterName5, componentVariableName5);
		parameterMapping4.put(functionParameterName6, componentVariableName6);

		final Set<Component> component4InputComponents = Sets.newLinkedHashSet();

		component4InputComponents.add(transformationComponent);
		component4InputComponents.add(transformationComponent2);

		final Component component4 = componentServiceTestUtils.createComponent(component4Name, parameterMapping4, function4,
				component4InputComponents, null);
		this.components.put(component4.getId(), component4);

		// transformation 2

		final String transformation2Name = "my transformation 2";
		final String transformation2Description = "transformation which makes use of three functions (two transformations and one funcion)";
		final String transformation2Parameter = "firstName";
		final String transformation2Parameter2 = "familyName";

		final Set<Component> components2 = Sets.newLinkedHashSet();

		final Iterator<Component> iter = component4.getInputComponents().iterator();

		components2.add(iter.next());
		components2.add(iter.next());
		components2.add(component4);

		final LinkedList<String> transformation2Parameters = Lists.newLinkedList();
		transformation2Parameters.add(transformation2Parameter);
		transformation2Parameters.add(transformation2Parameter2);

		final Transformation transformation2 = transformationServiceTestUtils.createTransformation(transformation2Name, transformation2Description,
				components2, transformation2Parameters);
		// (???) transformations.put(transformation2.getId(), transformation2);

		// attribute paths

		// input attribute paths

		final String dctermsCreatorId = "http://purl.org/dc/terms/creator";
		final String dctermsCreatorName = "creator";

		final Attribute dctermsCreator = attributeServiceTestUtils.createAttribute(dctermsCreatorId, dctermsCreatorName);
		attributes.put(dctermsCreator.getId(), dctermsCreator);

		// first name attribute path

		final String firstNameId = "http://xmlns.com/foaf/0.1/firstName";
		final String firstNameName = "firstName";

		final Attribute firstName = attributeServiceTestUtils.createAttribute(firstNameId, firstNameName);
		attributes.put(firstName.getId(), firstName);

		final LinkedList<Attribute> firstNameAttributePathList = Lists.newLinkedList();
		firstNameAttributePathList.add(dctermsCreator);
		firstNameAttributePathList.add(firstName);

		final AttributePath firstNameAttributePath = attributePathServiceTestUtils.createAttributePath(firstNameAttributePathList);
		attributePaths.put(firstNameAttributePath.getId(), firstNameAttributePath);

		// first name mapping attribute path instance

		final MappingAttributePathInstance firstNameMappingAttributePathInstance = mappingAttributePathInstanceServiceTestUtils
				.createMappingAttributePathInstance("first name mapping attribute path instance", firstNameAttributePath, null, null);
		mappingAttributePathInstances.put(firstNameMappingAttributePathInstance.getId(), firstNameMappingAttributePathInstance);

		// family name attribute path

		final String familyNameId = "http://xmlns.com/foaf/0.1/familyName";
		final String familyNameName = "familyName";

		final Attribute familyName = attributeServiceTestUtils.createAttribute(familyNameId, familyNameName);
		attributes.put(familyName.getId(), familyName);

		final LinkedList<Attribute> familyNameAttributePathList = Lists.newLinkedList();
		familyNameAttributePathList.add(dctermsCreator);
		familyNameAttributePathList.add(familyName);

		final AttributePath familyNameAttributePath = attributePathServiceTestUtils.createAttributePath(familyNameAttributePathList);
		attributePaths.put(familyNameAttributePath.getId(), familyNameAttributePath);

		// family name mapping attribute path instance

		final MappingAttributePathInstance familyNameMappingAttributePathInstance = mappingAttributePathInstanceServiceTestUtils
				.createMappingAttributePathInstance("family name mapping attribute path instance", familyNameAttributePath, null, null);
		mappingAttributePathInstances.put(familyNameMappingAttributePathInstance.getId(), familyNameMappingAttributePathInstance);

		// output attribute path

		final String foafNameId = "http://xmlns.com/foaf/0.1/name";
		final String foafNameName = "name";

		final Attribute foafName = attributeServiceTestUtils.createAttribute(foafNameId, foafNameName);
		attributes.put(foafName.getId(), foafName);

		final LinkedList<Attribute> nameAttributePathList = Lists.newLinkedList();
		nameAttributePathList.add(dctermsCreator);
		nameAttributePathList.add(foafName);

		final AttributePath nameAttributePath = attributePathServiceTestUtils.createAttributePath(nameAttributePathList);
		attributePaths.put(nameAttributePath.getId(), nameAttributePath);

		// output mapping attribute path instance

		final MappingAttributePathInstance outputMappingAttributePathInstance = mappingAttributePathInstanceServiceTestUtils
				.createMappingAttributePathInstance("output mapping attribute path instance", nameAttributePath, null, null);
		mappingAttributePathInstances.put(outputMappingAttributePathInstance.getId(), outputMappingAttributePathInstance);

		// transformation component

		final Map<String, String> transformationComponent3ParameterMappings = Maps.newLinkedHashMap();

		transformationComponent3ParameterMappings.put(transformation2Parameter, firstNameAttributePath.toAttributePath());
		transformationComponent3ParameterMappings.put(transformation2Parameter2, familyNameAttributePath.toAttributePath());
		transformationComponent3ParameterMappings.put("transformationOutputVariable", nameAttributePath.toAttributePath());

		final Component transformationComponent3 = componentServiceTestUtils.createComponent(transformation2.getName() + " (component)",
				transformationComponent3ParameterMappings, transformation2, null, null);
		this.components.put(transformationComponent3.getId(), transformationComponent3);

		// mapping

		final String mappingName = "my mapping";

		Mapping mapping = null;

		try {

			mapping = mappingService.createObjectTransactional().getObject();
		} catch (final DMPPersistenceException e) {

			assertTrue("something went wrong while mapping creation.\n" + e.getMessage(), false);
		}

		mapping.setName(mappingName);
		mapping.addInputAttributePath(firstNameMappingAttributePathInstance);
		mapping.addInputAttributePath(familyNameMappingAttributePathInstance);
		mapping.setOutputAttributePath(outputMappingAttributePathInstance);
		mapping.setTransformation(transformationComponent3);

		Mapping updatedMapping = null;

		try {

			updatedMapping = mappingService.updateObjectTransactional(mapping).getObject();
		} catch (final DMPPersistenceException e) {

			assertTrue("something went wrong while updating the mapping of id = '" + mapping.getId() + "'", false);
		}

		assertNotNull("the mapping shouldn't be null", updatedMapping);
		assertNotNull("the mapping name shouldn't be null", updatedMapping.getName());
		assertEquals("the mapping names are not equal", mappingName, updatedMapping.getName());
		assertNotNull("the transformation component id shouldn't be null", updatedMapping.getTransformation().getId());
		assertEquals("the transformation component ids are not equal", transformationComponent3.getId(), updatedMapping.getTransformation()
				.getId());
		assertNotNull("the transformation component parameter mappings shouldn't be null", updatedMapping.getTransformation()
				.getParameterMappings());
		assertEquals("the transformation component parameter mappings' size are not equal", 3, updatedMapping.getTransformation()
				.getParameterMappings().size());
		assertTrue("the transformation component parameter mappings doesn't contain a mapping for function parameter '"
				+ transformation2.getParameters().get(0) + "'",
				updatedMapping.getTransformation().getParameterMappings().containsKey(transformation2.getParameters().get(0)));
		assertEquals("the transformation component parameter mapping for '" + transformation2.getParameters().get(0) + "' are not equal",
				firstNameAttributePath.toAttributePath(),
				updatedMapping.getTransformation().getParameterMappings().get(transformation2.getParameters().get(0)));
		assertNotNull("the transformation shouldn't be null", updatedMapping.getTransformation().getFunction());
		assertNotNull("the transformation id shouldn't be null", updatedMapping.getTransformation().getFunction().getId());
		assertEquals("the transformation ids are not equal", transformation2.getId(), updatedMapping.getTransformation().getFunction().getId());
		assertNotNull("the transformation name shouldn't be null", updatedMapping.getTransformation().getFunction().getName());
		assertEquals("the transformation names are not equal", transformation2Name, updatedMapping.getTransformation().getFunction().getName());
		assertNotNull("the transformation description shouldn't be null", updatedMapping.getTransformation().getFunction().getDescription());
		assertEquals("the transformation descriptions are not equal", transformation2Description, updatedMapping.getTransformation()
				.getFunction().getDescription());
		assertEquals("the transformation parameters' size are not equal", 2, updatedMapping.getTransformation().getFunction().getParameters()
				.size());
		assertTrue("the transformation parameters doesn't contain transformation parameter '" + transformation2Parameter + "'", updatedMapping
				.getTransformation().getFunction().getParameters().contains(transformation2Parameter));
		assertEquals("the transformation parameter for '" + transformation2Parameter + "' are not equal", transformation2Parameter,
				updatedMapping.getTransformation().getFunction().getParameters().iterator().next());
		assertEquals("the function type is not '" + FunctionType.Transformation + "'", FunctionType.Transformation, updatedMapping
				.getTransformation().getFunction().getFunctionType());
		assertTrue("mapping transformation is not a '" + FunctionType.Transformation + "'",
				Transformation.class.isInstance(updatedMapping.getTransformation().getFunction()));
		assertNotNull("the transformation components set shouldn't be null", ((Transformation) updatedMapping.getTransformation()
				.getFunction()).getComponents());
		assertEquals("the transformation component sets are not equal", components2, ((Transformation) updatedMapping.getTransformation()
				.getFunction()).getComponents());
		assertNotNull("the component id shouldn't be null", ((Transformation) updatedMapping.getTransformation().getFunction())
				.getComponents().iterator().next().getId());

		final Iterator<Component> componentIter = ((Transformation) updatedMapping.getTransformation().getFunction()).getComponents().iterator();

		Component transformationComponentFromIter = null;

		while (componentIter.hasNext()) {

			final Component nextComponent = componentIter.next();

			if (nextComponent.getId().equals(transformationComponent.getId())) {

				transformationComponentFromIter = nextComponent;

				break;
			}
		}

		assertNotNull("the component shouldn't be null", transformationComponentFromIter);
		assertNotNull("the component name shouldn't be null", ((Transformation) updatedMapping.getTransformation().getFunction())
				.getComponents().iterator().next().getName());
		assertEquals("the component names are not equal", transformationComponentName, transformationComponentFromIter.getName());
		assertNotNull("the component parameter mappings shouldn't be null", transformationComponentFromIter.getParameterMappings());
		assertEquals("the component parameter mappings' size are not equal", 1, transformationComponentFromIter.getParameterMappings().size());
		assertTrue("the component parameter mappings doesn't contain a mapping for function parameter '"
				+ transformationComponentFunctionParameterName + "'",
				transformationComponentFromIter.getParameterMappings().containsKey(transformationComponentFunctionParameterName));
		assertEquals("the component parameter mapping for '" + transformationComponentFunctionParameterName + "' are not equal",
				transformationComponentVariableName,
				transformationComponentFromIter.getParameterMappings().get(transformationComponentFunctionParameterName));

		String json = null;

		try {

			json = objectMapper.writeValueAsString(updatedMapping);
		} catch (final JsonProcessingException e) {

			e.printStackTrace();
		}

		ProjectServiceTest.LOG.debug("mapping json: " + json);

		try {

			json = objectMapper.writeValueAsString(transformation2);
		} catch (final JsonProcessingException e) {

			e.printStackTrace();
		}

		ProjectServiceTest.LOG.debug("transformation json: " + json);

		try {

			json = objectMapper.writeValueAsString(transformation);
		} catch (final JsonProcessingException e) {

			e.printStackTrace();
		}

		ProjectServiceTest.LOG.debug("clean-up transformation json: " + json);

		try {

			json = objectMapper.writeValueAsString(component1);
		} catch (final JsonProcessingException e) {

			e.printStackTrace();
		}

		ProjectServiceTest.LOG.debug("clean-up previous component json: " + json);

		try {

			json = objectMapper.writeValueAsString(component);
		} catch (final JsonProcessingException e) {

			e.printStackTrace();
		}

		ProjectServiceTest.LOG.debug("clean-up main component json: " + json);

		try {

			json = objectMapper.writeValueAsString(component2);
		} catch (final JsonProcessingException e) {

			e.printStackTrace();
		}

		ProjectServiceTest.LOG.debug("clean-up next component json: " + json);

		mappings.put(updatedMapping.getId(), updatedMapping);

		return updatedMapping;
	}

	private DataModel createInputDataModel() throws Exception {

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
		final SchemaAttributePathInstance attributePathInstance1 = 
				schemaAttributePathInstanceServiceTestUtils.createSchemaAttributePathInstance("SAPI-1", attributePath1, null);
		schemaAttributePathInstances.put(attributePathInstance1.getId(), attributePathInstance1);

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
		final SchemaAttributePathInstance attributePathInstance2 = 
				schemaAttributePathInstanceServiceTestUtils.createSchemaAttributePathInstance("SAPI-2", attributePath2, null);
		schemaAttributePathInstances.put(attributePathInstance2.getId(), attributePathInstance2);

		// third attribute path

		final String dctermsCreatedId = "http://purl.org/dc/terms/created";
		final String dctermsCreatedName = "created";

		final Attribute dctermsCreated = attributeServiceTestUtils.createAttribute(dctermsCreatedId, dctermsCreatedName);
		attributes.put(dctermsCreated.getId(), dctermsCreated);

		final LinkedList<Attribute> attributePath3Arg = Lists.newLinkedList();

		attributePath3Arg.add(dctermsCreated);

		System.out.println("attribute created = '" + dctermsCreated.toString());

		final AttributePath attributePath3 = attributePathServiceTestUtils.createAttributePath(attributePath3Arg);
		final SchemaAttributePathInstance attributePathInstance3 = 
				schemaAttributePathInstanceServiceTestUtils.createSchemaAttributePathInstance("SAPI-3", attributePath3, null);
		schemaAttributePathInstances.put(attributePathInstance3.getId(), attributePathInstance3);

		// record class

		final String biboDocumentId = "http://purl.org/ontology/bibo/Document";
		final String biboDocumentName = "document";

		final Clasz biboDocument = claszServiceTestUtils.createClass(biboDocumentId, biboDocumentName);
		classes.put(biboDocument.getId(), biboDocument);

		// schema

		final Set<SchemaAttributePathInstance> attributePaths = Sets.newLinkedHashSet();

		attributePaths.add(attributePathInstance1);
		attributePaths.add(attributePathInstance2);
		attributePaths.add(attributePathInstance2);

		final Schema schema = schemaServiceTestUtils.createSchema("my schema", attributePaths, biboDocument);
		schemas.put(schema.getId(), schema);

		// configuration

		final ObjectNode parameters = new ObjectNode(objectMapper.getNodeFactory());
		final String parameterKey = "fileseparator";
		final String parameterValue = ";";
		parameters.put(parameterKey, parameterValue);

		final Configuration configuration = configurationServiceTestUtils.createConfiguration("my configuration", "configuration description",
				parameters);
		configurations.put(configuration.getId(), configuration);

		// data resource

		final String attributeKey = "path";
		final String attributeValue = "/path/to/file.end";

		final ObjectNode attributes = new ObjectNode(DMPPersistenceUtil.getJSONFactory());
		attributes.put(attributeKey, attributeValue);

		final Set<Configuration> configurations = Sets.newLinkedHashSet();
		configurations.add(configuration);

		final Resource resource = resourceServiceTestUtils.createResource("bla", "blubblub", ResourceType.FILE, attributes, configurations);
		resources.put(resource.getId(), resource);

		// data model

		final DataModelService dataModelService = GuicedTest.injector.getInstance(DataModelService.class);

		DataModel dataModel = null;

		try {

			dataModel = dataModelService.createObjectTransactional().getObject();
		} catch (final DMPPersistenceException e) {

			assertTrue("something went wrong while data model creation.\n" + e.getMessage(), false);
		}

		final String dataModelName = "my data model";
		final String dataModelDescription = "my data model description";

		dataModel.setName(dataModelName);
		dataModel.setDescription(dataModelDescription);
		dataModel.setDataResource(resource);
		dataModel.setConfiguration(configuration);
		dataModel.setSchema(schema);

		DataModel updatedDataModel = null;

		try {

			updatedDataModel = dataModelService.updateObjectTransactional(dataModel).getObject();
		} catch (final DMPPersistenceException e) {

			assertTrue("something went wrong while updating the data model of id = '" + dataModel.getId() + "'", false);
		}

		
		final SchemaAttributePathInstance attributePathInstanceFromSchema 
			= dataModel.getSchema().getAttributePathInstance(attributePathInstance1.getId());
		final SchemaAttributePathInstance attributePathInstanceFromUpdatedSchema
			= updatedDataModel.getSchema().getAttributePathInstance(attributePathInstance1.getId());

		assertNotNull("the updated data model shouldn't be null", updatedDataModel);
		assertNotNull("the update data model id shouldn't be null", updatedDataModel.getId());
		assertNotNull("the schema of the updated data model shouldn't be null", updatedDataModel.getSchema());
		assertNotNull("the schema's attribute paths of the updated schema shouldn't be null", updatedDataModel.getSchema().getUniqueAttributePathInstances());
		
		assertEquals("the attribute path instance sets of the schema are not equal",
				schema.getUniqueAttributePathInstances(), updatedDataModel.getSchema().getUniqueAttributePathInstances());
	
		assertEquals("the attribute path instance '" + attributePathInstance1.getId() + "' of the schema are not equal",
				attributePathInstanceFromSchema, attributePathInstanceFromUpdatedSchema);
		
		assertNotNull("the attribute path's attributes of the attribute path '" + attributePathInstance1.getId()
				+ "' of the updated schema shouldn't be null",
				attributePathInstanceFromUpdatedSchema.getAttributePath().getAttributes());
		
		assertEquals("the attribute of attribute path instance '" + attributePathInstance1.getId() + "' are not equal",
				attributePathInstance1.getAttributePath().getAttributes(),
				attributePathInstanceFromUpdatedSchema.getAttributePath().getAttributes());
		
		assertEquals("the first attributes of attribute path instance '" + attributePathInstance1.getId() + "' are not equal", attributePath1
				.getAttributePath().get(0),
				attributePathInstanceFromUpdatedSchema.getAttributePath().getAttributePath().get(0));
		
		assertNotNull("the attribute path string of attribute path instance '" + attributePathInstance1.getId() + "' of the update schema shouldn't be null",
				attributePathInstanceFromUpdatedSchema.getAttributePath().toAttributePath());
		
		assertEquals("the attribute path strings of attribute path instance '" + attributePathInstance1.getId() + "' are not equal",
				attributePathInstance1.getAttributePath().toAttributePath(), attributePathInstanceFromUpdatedSchema.getAttributePath().toAttributePath());
		
		assertNotNull("the record class of the updated schema shouldn't be null",
				updatedDataModel.getSchema().getRecordClass());
		
		assertEquals("the record classes are not equal", schema.getRecordClass(),
				updatedDataModel.getSchema().getRecordClass());
		
		assertNotNull("the resource of the updated data model shouddn't be null",
				updatedDataModel.getDataResource());

		resourceServiceTestUtils.checkSimpleResource(resource, updatedDataModel.getDataResource(), attributeKey, attributeValue);
		resourceServiceTestUtils.checkComplexResource(resource, updatedDataModel.getDataResource());
		resourceServiceTestUtils.checkComplexResource(resource, updatedDataModel.getDataResource(), parameterKey, parameterValue);

		assertNotNull("the configuration of the updated data model shouldn't be null", updatedDataModel.getConfiguration());
		assertNotNull("the configuration name of the updated resource shouldn't be null", updatedDataModel.getConfiguration().getName());
		assertEquals("the configuration' names of the resource are not equal", configuration.getName(), updatedDataModel.getConfiguration()
				.getName());
		assertNotNull("the configuration description of the updated resource shouldn't be null", updatedDataModel.getConfiguration()
				.getDescription());
		assertEquals("the configuration descriptions of the resource are not equal", configuration.getDescription(), updatedDataModel
				.getConfiguration().getDescription());
		assertNotNull("the configuration parameters of the updated resource shouldn't be null", updatedDataModel.getConfiguration()
				.getParameters());
		assertEquals("the configurations parameters of the resource are not equal", configuration.getParameters(), updatedDataModel
				.getConfiguration().getParameters());
		assertNotNull("the parameter value shouldn't be null", configuration.getParameter(parameterKey));
		assertEquals("the parameter value should be equal", configuration.getParameter(parameterKey).asText(), parameterValue);

		String json = null;

		try {

			json = objectMapper.writeValueAsString(updatedDataModel);
		} catch (final JsonProcessingException e) {

			e.printStackTrace();
		}

		ProjectServiceTest.LOG.debug("data model json: " + json);

		return updatedDataModel;
	}

	private DataModel createOutputDataModel() throws Exception {

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
		final SchemaAttributePathInstance attributePathInstance1 = 
				schemaAttributePathInstanceServiceTestUtils.createSchemaAttributePathInstance("SAPI-1", attributePath1, null);
		schemaAttributePathInstances.put(attributePathInstance1.getId(), attributePathInstance1);
		
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
		final SchemaAttributePathInstance attributePathInstance2 = 
				schemaAttributePathInstanceServiceTestUtils.createSchemaAttributePathInstance("SAPI-2", attributePath2, null);
		schemaAttributePathInstances.put(attributePathInstance2.getId(), attributePathInstance2);
		
		// third attribute path

		final String dctermsCreatedId = "http://purl.org/dc/terms/created";
		final String dctermsCreatedName = "created";

		final Attribute dctermsCreated = attributeServiceTestUtils.createAttribute(dctermsCreatedId, dctermsCreatedName);
		attributes.put(dctermsCreated.getId(), dctermsCreated);

		final LinkedList<Attribute> attributePath3Arg = Lists.newLinkedList();

		attributePath3Arg.add(dctermsCreated);

		System.out.println("attribute created = '" + dctermsCreated.toString());

		final AttributePath attributePath3 = attributePathServiceTestUtils.createAttributePath(attributePath3Arg);
		final SchemaAttributePathInstance attributePathInstance3 = 
				schemaAttributePathInstanceServiceTestUtils.createSchemaAttributePathInstance("SAPI-3", attributePath3, null);
		schemaAttributePathInstances.put(attributePathInstance3.getId(), attributePathInstance3);
		
		// record class

		final String biboDocumentId = "http://purl.org/ontology/bibo/Document";
		final String biboDocumentName = "document";

		final Clasz biboDocument = claszServiceTestUtils.createClass(biboDocumentId, biboDocumentName);
		classes.put(biboDocument.getId(), biboDocument);

		// schema

		final Set<SchemaAttributePathInstance> attributePaths = Sets.newLinkedHashSet();

		attributePaths.add(attributePathInstance1);
		attributePaths.add(attributePathInstance2);
		attributePaths.add(attributePathInstance3);

		final Schema schema = schemaServiceTestUtils.createSchema("my schema", attributePaths, biboDocument);
		schemas.put(schema.getId(), schema);

		// data model

		final DataModelService dataModelService = GuicedTest.injector.getInstance(DataModelService.class);

		DataModel dataModel = null;

		try {

			dataModel = dataModelService.createObjectTransactional().getObject();
		} catch (final DMPPersistenceException e) {

			assertTrue("something went wrong while data model creation.\n" + e.getMessage(), false);
		}

		final String dataModelName = "my output data model";
		final String dataModelDescription = "my output data model description";

		dataModel.setName(dataModelName);
		dataModel.setDescription(dataModelDescription);
		dataModel.setSchema(schema);

		DataModel updatedDataModel = null;

		try {

			updatedDataModel = dataModelService.updateObjectTransactional(dataModel).getObject();
		} catch (final DMPPersistenceException e) {

			assertTrue("something went wrong while updating the data model of id = '" + dataModel.getId() + "'", false);
		}

		assertNotNull("the updated data model shouldn't be null", updatedDataModel);
		assertNotNull("the update data model id shouldn't be null", updatedDataModel.getId());
		assertNotNull("the schema of the updated data model shouldn't be null", updatedDataModel.getSchema());
		assertNotNull("the schema's attribute paths of the updated schema shouldn't be null", updatedDataModel.getSchema().getUniqueAttributePathInstances());
		assertEquals("the schema's attribute paths size are not equal", schema.getUniqueAttributePathInstances(), updatedDataModel.getSchema()
				.getUniqueAttributePathInstances());
		assertEquals("the attribute path instances '" + attributePathInstance1.getId() + "' of the schema are not equal",
				schema.getAttributePathInstance(attributePathInstance1.getId()), updatedDataModel.getSchema().getAttributePathInstance(attributePathInstance1.getId()));
		assertNotNull("the attribute path's attributes of the attribute path '" + attributePathInstance1.getId()
				+ "' of the updated schema shouldn't be null", updatedDataModel.getSchema().getAttributePathInstance(attributePathInstance1.getId()).getAttributePath().getAttributes());
		assertEquals("the attribute path's attributes size of attribute path '" + attributePathInstance1.getId() + "' are not equal",
				attributePath1.getAttributes(), updatedDataModel.getSchema().getAttributePathInstance(attributePathInstance1.getId()).getAttributePath().getAttributes());
		assertEquals("the first attributes of attribute path '" + attributePath1.getId() + "' are not equal", attributePathInstance1.getAttributePath()
				.getAttributePath().get(0), updatedDataModel.getSchema().getAttributePathInstance(attributePathInstance1.getId()).getAttributePath().getAttributePath().get(0));
		assertNotNull("the attribute path string of attribute path instance '" + attributePathInstance1.getId() + "' of the update schema shouldn't be null",
				updatedDataModel.getSchema().getAttributePathInstance(attributePathInstance1.getId()).getAttributePath().toAttributePath());
		assertEquals("the attribute path's strings attribute path '" + attributePath1.getId() + "' are not equal",
				attributePath1.toAttributePath(), updatedDataModel.getSchema().getAttributePathInstance(attributePathInstance1.getId()).getAttributePath().toAttributePath());
		assertNotNull("the record class of the updated schema shouldn't be null", updatedDataModel.getSchema().getRecordClass());
		assertEquals("the recod classes are not equal", schema.getRecordClass(), updatedDataModel.getSchema().getRecordClass());

		String json = null;

		try {

			json = objectMapper.writeValueAsString(updatedDataModel);
		} catch (final JsonProcessingException e) {

			e.printStackTrace();
		}

		ProjectServiceTest.LOG.debug("data model json: " + json);

		return updatedDataModel;
	}
}
