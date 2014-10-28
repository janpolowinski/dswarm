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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.Assert;

import org.dswarm.persistence.model.job.Component;
import org.dswarm.persistence.model.job.Filter;
import org.dswarm.persistence.model.job.Function;
import org.dswarm.persistence.model.job.Mapping;
import org.dswarm.persistence.model.job.Transformation;
import org.dswarm.persistence.model.job.proxy.ProxyMapping;
import org.dswarm.persistence.model.schema.Attribute;
import org.dswarm.persistence.model.schema.AttributePath;
import org.dswarm.persistence.model.schema.MappingAttributePathInstance;
import org.dswarm.persistence.service.job.MappingService;
import org.dswarm.persistence.service.job.test.MappingServiceTest;
import org.dswarm.persistence.service.schema.test.utils.AttributePathServiceTestUtils;
import org.dswarm.persistence.service.schema.test.utils.AttributeServiceTestUtils;
import org.dswarm.persistence.service.schema.test.utils.MappingAttributePathInstanceServiceTestUtils;
import org.dswarm.persistence.service.test.utils.BasicDMPJPAServiceTestUtils;

public class MappingServiceTestUtils extends BasicDMPJPAServiceTestUtils<MappingService, ProxyMapping, Mapping> {

	private final ComponentServiceTestUtils						componentServiceTestUtils;

	private final MappingAttributePathInstanceServiceTestUtils	mappingAttributePathInstanceServiceTestUtils;
	private final AttributeServiceTestUtils attributeServiceTestUtils;
	private final AttributePathServiceTestUtils  attributePathServiceTestUtils;
	private final FunctionServiceTestUtils functionServiceTestUtils;
	private final FilterServiceTestUtils	filterServiceTestUtils;
	
	private final TransformationServiceTestUtils	transformationServiceTestUtils;

	private final Set<Long>										checkedExpectedAttributePaths	= Sets.newHashSet();

	private final Set<Long>										checkedActualAttributePaths		= Sets.newHashSet();




	public MappingServiceTestUtils() {

		super(Mapping.class, MappingService.class);

		componentServiceTestUtils = new ComponentServiceTestUtils();
		mappingAttributePathInstanceServiceTestUtils = new MappingAttributePathInstanceServiceTestUtils();
		transformationServiceTestUtils = new TransformationServiceTestUtils();
		attributeServiceTestUtils = new AttributeServiceTestUtils();
		attributePathServiceTestUtils = new AttributePathServiceTestUtils();
		functionServiceTestUtils = new FunctionServiceTestUtils();
		filterServiceTestUtils = new FilterServiceTestUtils();
	}

	/**
	 * {@inheritDoc} <br />
	 * Assert that both mappings either have no or equal (transformation) components, see
	 * {@link ComponentServiceTestUtils#compareObjects(Component, Component)}. <br />
	 * Assert that both mappings either have no or equal input attribute paths, see {@link
	 * BasicJPAServiceTestUtils.compareObjects(Set, Map)}. <br />
	 * Assert that both mappings either have no or equal output attribute paths, see {@link
	 * BasicJPAServiceTestUtils.compareObjects(Set, Map)}. <br />
	 */
	@Override
	public void compareObjects(final Mapping expectedMapping, final Mapping actualMapping) {

		super.compareObjects(expectedMapping, actualMapping);

		// transformation (component)
		if (expectedMapping.getTransformation() == null) {

			Assert.assertNull("the actual mapping '" + actualMapping.getId() + "' shouldn't have a transformation", actualMapping.getTransformation());

		} else {
			componentServiceTestUtils.compareObjects(expectedMapping.getTransformation(), actualMapping.getTransformation());
		}

		// input attribute paths
		if (expectedMapping.getInputAttributePaths() == null || expectedMapping.getInputAttributePaths().isEmpty()) {

			final boolean actualMappingHasNoInputAttributePaths = (actualMapping.getInputAttributePaths() == null || actualMapping
					.getInputAttributePaths().isEmpty());
			Assert.assertTrue("actual mapping '" + actualMapping.getId() + "' shouldn't have input attribute paths",
					actualMappingHasNoInputAttributePaths);

		} else { // !null && !empty

			final Set<MappingAttributePathInstance> actualInputAttributePaths = actualMapping.getInputAttributePaths();

			Assert.assertNotNull("input attribute paths of actual mapping '" + actualMapping.getId() + "' shouldn't be null",
					actualInputAttributePaths);
			Assert.assertFalse("input attribute paths of actual mapping '" + actualMapping.getId() + "' shouldn't be empty",
					actualInputAttributePaths.isEmpty());

			final Map<Long, MappingAttributePathInstance> actualInputAttributePathsMap = Maps.newHashMap();

			for (final MappingAttributePathInstance actualInputAttributePath : actualInputAttributePaths) {

				if (checkAttributePath(actualInputAttributePath, checkedActualAttributePaths)) {

					// SR FIXME why can we be sure we dont need to check this actualInputAttributePath? the last reset() may have
					// been a while ago.
					continue;
				}

				actualInputAttributePathsMap.put(actualInputAttributePath.getId(), actualInputAttributePath);
			}

			final Set<MappingAttributePathInstance> uncheckedExpectedInputAttributePaths = Sets.newHashSet();

			for (final MappingAttributePathInstance expectedInputAttributePath : expectedMapping.getInputAttributePaths()) {

				if (checkAttributePath(expectedInputAttributePath, checkedExpectedAttributePaths)) {

					// SR FIXME why can we be sure we dont need to check this expectedInputAttributePath? the last reset() may
					// have been a while ago.
					continue;
				}

				uncheckedExpectedInputAttributePaths.add(expectedInputAttributePath);

			}

			mappingAttributePathInstanceServiceTestUtils.compareObjects(uncheckedExpectedInputAttributePaths, actualInputAttributePathsMap);
		}

		// output attribute paths
		if (expectedMapping.getOutputAttributePath() == null) {

			Assert.assertNull("actual mapping '" + actualMapping.getId() + "' shouldn't have an output attribute path",
					actualMapping.getOutputAttributePath());

			// SR FIXME why can we skip here?
		} else if (!checkAttributePath(expectedMapping.getOutputAttributePath(), checkedExpectedAttributePaths)
				&& !checkAttributePath(actualMapping.getOutputAttributePath(), checkedActualAttributePaths)) {

			mappingAttributePathInstanceServiceTestUtils.compareObjects(expectedMapping.getOutputAttributePath(),
					actualMapping.getOutputAttributePath());

		}
	}

	private boolean checkAttributePath(final MappingAttributePathInstance attributePath, final Set<Long> checkedAttributePaths) {

		if (attributePath != null && attributePath.getId() != null) {

			if (checkedAttributePaths.contains(attributePath.getId())) {

				// attribute path was already checked

				return true;
			}

			checkedAttributePaths.add(attributePath.getId());
		}

		return false;
	}

	/**
	 * {@inheritDoc}<br/>
	 * Updates the name, transformation (component), input attribute paths and output attribute path of the mapping.
	 */
	@Override
	protected Mapping prepareObjectForUpdate(final Mapping objectWithUpdates, final Mapping object) {

		super.prepareObjectForUpdate(objectWithUpdates, object);

		object.setTransformation(objectWithUpdates.getTransformation());
		object.setInputAttributePaths(objectWithUpdates.getInputAttributePaths());
		object.setOutputAttributePath(objectWithUpdates.getOutputAttributePath());

		return object;
	}

	@Override
	public void reset() {

		checkedExpectedAttributePaths.clear();
		checkedActualAttributePaths.clear();
		// checkedExpectedFilters.clear();
		// checkedActualFilters.clear();

		mappingAttributePathInstanceServiceTestUtils.reset();
		// filtersResourceTestUtils.reset();
		componentServiceTestUtils.reset();
	}
	
	/**
	 * taken from on MappingServiceTest.simplyMapping()
	 * 
	 * @return
	 * @throws Exception 
	 */
	public Mapping getExampleMapping1() throws Exception {
		
		final LinkedList<String> parameters = Lists.newLinkedList();

		parameters.add("inputString");

		final Function function = functionServiceTestUtils.createFunction("trim", "trims leading and trailing whitespaces from a given string",
				parameters);

		final String componentName = "my trim component";
		final Map<String, String> parameterMappings = Maps.newLinkedHashMap();

		final String functionParameterName = "inputString";
		final String componentVariableName = "previousComponent.outputString";

		parameterMappings.put(functionParameterName, componentVariableName);

		final Component component = componentServiceTestUtils.createComponent(componentName, parameterMappings, function, null, null);

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

		// attribute paths

		// input attribute path

		final String dctermsTitleId = "http://purl.org/dc/terms/title";
		final String dctermsTitleName = "title";

		final Attribute dctermsTitle = attributeServiceTestUtils.createAttribute(dctermsTitleId, dctermsTitleName);

		final LinkedList<Attribute> dctermsTitleAttributePath = Lists.newLinkedList();
		dctermsTitleAttributePath.add(dctermsTitle);

		final AttributePath inputAttributePath = attributePathServiceTestUtils.createAttributePath(dctermsTitleAttributePath);

		// input mapping attribute path instance

		final MappingAttributePathInstance inputMappingAttributePathInstance = mappingAttributePathInstanceServiceTestUtils
				.createMappingAttributePathInstance("input mapping attribute path instance", inputAttributePath, null, null);

		// output attribute path

		final String rdfsLabelId = "http://www.w3.org/2000/01/rdf-schema#label";
		final String rdfsLabelName = "label";

		final Attribute rdfsLabel = attributeServiceTestUtils.createAttribute(rdfsLabelId, rdfsLabelName);

		final LinkedList<Attribute> rdfsLabelAttributePath = Lists.newLinkedList();
		rdfsLabelAttributePath.add(rdfsLabel);

		final AttributePath outputAttributePath = attributePathServiceTestUtils.createAttributePath(rdfsLabelAttributePath);

		// output mapping attribute path instance

		final MappingAttributePathInstance outputMappingAttributePathInstance = mappingAttributePathInstanceServiceTestUtils
				.createMappingAttributePathInstance("output mapping attribute path instance", outputAttributePath, null, null);

		// transformation component

		final Map<String, String> transformationComponentParameterMappings = Maps.newLinkedHashMap();

		transformationComponentParameterMappings.put(transformation.getParameters().get(0), inputAttributePath.toAttributePath());
		transformationComponentParameterMappings.put("transformationOutputVariable", outputAttributePath.toAttributePath());

		final Component transformationComponent = componentServiceTestUtils.createComponent(transformation.getName() + " (component)",
				transformationComponentParameterMappings, transformation, null, null);

		// mapping

		final String mappingName = "my mapping";

		final Mapping mapping = createObject().getObject();
		mapping.setName(mappingName);
		mapping.addInputAttributePath(inputMappingAttributePathInstance);
		mapping.setOutputAttributePath(outputMappingAttributePathInstance);
		mapping.setTransformation(transformationComponent);

		final Mapping updatedMapping = updateObjectTransactional(mapping).getObject();
		
		return updatedMapping;
	}

	/**
	 * as needed to replace mapping in task.filter.json 
	 * 
	 * @throws Exception 
	 */
	public Mapping getExampleMapping2() throws Exception {
		
		// concat function and component
		
		final LinkedList<String> parameters = Lists.newLinkedList();
	
		parameters.add("delimiter");
		parameters.add("prefix");
		parameters.add("postfix");
	
		final Function function = functionServiceTestUtils.createFunction("concat", "Collects all received values and concatenates them on record end.",
				parameters);
	
		final String componentName = "my concat component";
		final Map<String, String> parameterMappings = Maps.newLinkedHashMap();
	
		final String functionParameterName1 = "inputString";
		final String componentVariableName1 = "feldnr,feldvalue";
		
		final String functionParameterName2 = "delimiter";
		final String componentVariableName2 = ",";
	
		parameterMappings.put(functionParameterName1, componentVariableName1);
		parameterMappings.put(functionParameterName2, componentVariableName2);
	
		final Component component = componentServiceTestUtils.createComponent(componentName, parameterMappings, function, null, null);
	
		// transformation
	
		final String transformationName = "Nr->Nr transformation";
		final String transformationDescription = "Nr->Nr";
		final String transformationParameter1 = "feldvalue";
		final String transformationParameter2 = "feldnr";
		final String transformationParameter3 = "__TRANSFORMATION_OUTPUT_VARIABLE__1"; // TODO make this special variable name some constant at least!
	
		final LinkedList<String> transformationParameters = Lists.newLinkedList();
		transformationParameters.add(transformationParameter1);
		transformationParameters.add(transformationParameter2);
		transformationParameters.add(transformationParameter3);
	
		final Set<Component> components = Sets.newLinkedHashSet();
	
		components.add(component);
	
		final Transformation transformation = transformationServiceTestUtils.createTransformation(transformationName, transformationDescription,
				components, transformationParameters);
	
		// attribute paths
	
		// input attribute path
		
		final Attribute mabFeld = attributeServiceTestUtils.createAttribute(
				"http://www.ddb.de/professionell/mabxml/mabxml-1.xsd#feld", "feld");
		final Attribute mabNr = attributeServiceTestUtils.createAttribute(
				"http://www.ddb.de/professionell/mabxml/mabxml-1.xsd#nr", "nr");
		final Attribute mabValue = attributeServiceTestUtils.createAttribute(
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#value", "value");
		
		final LinkedList<Attribute> feldNrList = Lists.newLinkedList();
		feldNrList.add(mabFeld);
		feldNrList.add(mabNr);
		
		final LinkedList<Attribute> feldValueList = Lists.newLinkedList();
		feldValueList.add(mabFeld);
		feldValueList.add(mabValue);
		
		final AttributePath feldNr = attributePathServiceTestUtils.createAttributePath(feldNrList);
		final AttributePath feldValue = attributePathServiceTestUtils.createAttributePath(feldValueList);
		
		final MappingAttributePathInstance feldNrAttributePathInstance = mappingAttributePathInstanceServiceTestUtils
				.createMappingAttributePathInstance("feldnr", feldNr, null, null);
		
		final Filter filter = filterServiceTestUtils.createFilter("filter mabxml attributes nr and ind",
				"[{&quot;http://www.ddb.de/professionell/mabxml/mabxml-1.xsd#feld&amp;#30;http://www.ddb.de/professionell/mabxml/mabxml-1.xsd#nr&quot;: &quot;077&quot;}, {&quot;http://www.ddb.de/professionell/mabxml/mabxml-1.xsd#feld&amp;#30;http://www.ddb.de/professionell/mabxml/mabxml-1.xsd#ind&quot;: &quot;p&quot;}]");
		
		final MappingAttributePathInstance feldValueAttributePathInstance = mappingAttributePathInstanceServiceTestUtils
				.createMappingAttributePathInstance("feldvalue", feldValue, 2, filter);
		
	
		// output mapping attribute path instance
	
		final String outputMappingAttributePathInstanceName = "output mapping attribute path instance";
		
		final MappingAttributePathInstance outputMappingAttributePathInstance = mappingAttributePathInstanceServiceTestUtils
				.createMappingAttributePathInstance(outputMappingAttributePathInstanceName, feldNr, null, null);
	
		// transformation component
		final String transformationComponentParameter1 = "feldvalue";
		final String transformationComponentParameter2 = "feldnr";
		final String transformationComponentParameter3 = outputMappingAttributePathInstanceName;
//		final List<String> transformationComponentParameters = Lists.newArrayList();
//		transformationComponentParameters.add(transformationComponentParameter1);
//		transformationComponentParameters.add(transformationComponentParameter2);
//		transformationComponentParameters.add(transformationComponentParameter3);
	
		final Map<String, String> transformationComponentParameterMappings = Maps.newLinkedHashMap();
	
		//transformationComponentParameterMappings.put(transformation.getParameters().get(0), feldNr.toAttributePath());
		transformationComponentParameterMappings.put(transformationParameter1, transformationComponentParameter1);
		transformationComponentParameterMappings.put(transformationParameter2, transformationComponentParameter2);
		transformationComponentParameterMappings.put(transformationParameter3, transformationComponentParameter3);
	
		final Component transformationComponent = componentServiceTestUtils.createComponent(transformation.getName() + " (component)",
				transformationComponentParameterMappings, transformation, null, null);
	
		// mapping
	
		final String mappingName = "Nr->Nr";
	
		final Mapping mapping = createObject().getObject();
		mapping.setName(mappingName);
		mapping.addInputAttributePath(feldNrAttributePathInstance);
		mapping.addInputAttributePath(feldValueAttributePathInstance);
		mapping.setOutputAttributePath(outputMappingAttributePathInstance);
		mapping.setTransformation(transformationComponent);
	
		final Mapping updatedMapping = updateObjectTransactional(mapping).getObject();
		
		return updatedMapping;
	}
}
