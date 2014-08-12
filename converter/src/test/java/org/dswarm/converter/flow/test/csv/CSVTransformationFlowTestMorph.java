package org.dswarm.converter.flow.test.csv;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Provider;
import org.junit.Assert;

import org.dswarm.converter.GuicedTest;
import org.dswarm.converter.flow.TransformationFlow;
import org.dswarm.persistence.service.InternalModelServiceFactory;
import org.dswarm.persistence.util.DMPPersistenceUtil;


/**
 * @author polowins
 * 
 * Test for sketching a morph script to produce nested, structured output data.
 * 
 */
public class CSVTransformationFlowTestMorph extends GuicedTest {

	/**
	 * Test for sketching a morph script to produce nested, structured output data.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConstructionOfStructuredOutputWithMorph() throws Exception {

		executeCSVMorphWithTuples(null, "dd-700/structured-output.xml", "dd-700/test_book_with_author_data_flat.tuples.json");
		//testCSVMorphWithTuples("dd-700/test_book_with_author_data.result.json", "dd-700/structured-output.xml", "dd-700/test_book_with_author_data_flat.tuples.json");
	}
	
	/**
	 * Test this test with some substring replacement morph.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testOldSubStringReplaceWithMorph() throws Exception {

		testCSVMorphWithTuples("dd-700/test_transf2.result.json", "dd-700/substring-replace.xml", "dd-700/test_transf2.tuples.json");
	}

	
	
	private void testCSVMorphWithTuples(final String resultJSONFileName, final String morphXMLFileName, final String tuplesJSONFileName)
			throws Exception {

		final String expected = DMPPersistenceUtil.getResourceAsString(resultJSONFileName);

		final Provider<InternalModelServiceFactory> internalModelServiceFactoryProvider = GuicedTest.injector
				.getProvider(InternalModelServiceFactory.class);

		final String finalMorphXmlString = DMPPersistenceUtil.getResourceAsString(morphXMLFileName);

		// looks like that the utilised ObjectMappers getting a bit mixed, i.e., actual sometimes delivers a result that is not in
		// pretty print and sometimes it is in pretty print ... (that's why the reformatting of both - expected and actual)
		final ObjectMapper objectMapper2 = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).configure(
				SerializationFeature.INDENT_OUTPUT, true);

		// final Task task = objectMapper.readValue(finalTaskJSONString, Task.class);

		final TransformationFlow flow = TransformationFlow.fromString(finalMorphXmlString, internalModelServiceFactoryProvider);

		flow.getScript();

		final String actual = flow.applyResource(tuplesJSONFileName);
		final ArrayNode array = objectMapper2.readValue(actual, ArrayNode.class);
		final String finalActual = objectMapper2.writeValueAsString(array);

		final ArrayNode expectedArray = objectMapper2.readValue(expected, ArrayNode.class);
		final String finalExpected = objectMapper2.writeValueAsString(expectedArray);

		Assert.assertEquals(finalExpected.length(), finalActual.length());

	}
	
	private void executeCSVMorphWithTuples(final String resultJSONFileName, final String morphXMLFileName, final String tuplesJSONFileName)
			throws Exception {

		final Provider<InternalModelServiceFactory> internalModelServiceFactoryProvider = GuicedTest.injector
				.getProvider(InternalModelServiceFactory.class);

		final String finalMorphXmlString = DMPPersistenceUtil.getResourceAsString(morphXMLFileName);

		// looks like that the utilised ObjectMappers getting a bit mixed, i.e., actual sometimes delivers a result that is not in
		// pretty print and sometimes it is in pretty print ... (that's why the reformatting of both - expected and actual)
		final ObjectMapper objectMapper2 = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).configure(
				SerializationFeature.INDENT_OUTPUT, true);

		// final Task task = objectMapper.readValue(finalTaskJSONString, Task.class);

		final TransformationFlow flow = TransformationFlow.fromString(finalMorphXmlString, internalModelServiceFactoryProvider);

		flow.getScript();

		final String actual = flow.applyResource(tuplesJSONFileName);
		final ArrayNode array = objectMapper2.readValue(actual, ArrayNode.class);
		final String finalActual = objectMapper2.writeValueAsString(array);

		System.out.println(finalActual);
	}


}
