package org.dswarm.converter.flow.test.csv;

import org.junit.Ignore;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Provider;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private static final Logger	LOG	= LoggerFactory.getLogger(CSVTransformationFlowTestMorph.class);
	

	/**
	 * Test for sketching a morph script to produce structured output data with entities 
	 * (but with no nested subentities).
	 * 
	 * @throws Exception
	 */
	@Test 
	public void testConstructionOfStructuredOutputWithMorph() throws Exception {

		testCSVMorphWithTuples(
				"dd-700/test_book_with_author_data.result.json",
				"dd-700/structured-output.xml",
				"dd-700/test_book_with_author_data_flat.tuples.json");
	}
	
	/**
	 * Test for sketching a morph script to produce nested, structured output data 
	 * with subentities (e.g. someBook -author-> somePerson -bornIn-> somePlace) .
	 * 
	 * @throws Exception
	 */
	@Test
	//@Ignore // does not yet work, since subentities
	public void testConstructionOfStructuredNestedOutputWithMorph() throws Exception {

		testCSVMorphWithTuples(
				"dd-700/test_book_with_author_data.result_subentities.json",
				"dd-700/structured-output-subentities.xml",
				"dd-700/test_book_with_author_data_flat.tuples.json");
	}
	

	/**
	 * NOTE: Does not yet work when streamunflattener are used. If streamunflattener and collapser are outcommented
	 * it works.
	 * 
	 * @param resultJSONFileName
	 * @param morphXMLFileName
	 * @param tuplesJSONFileName
	 * @throws Exception
	 */
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

		final TransformationFlow flow = TransformationFlow.fromString(finalMorphXmlString, internalModelServiceFactoryProvider);

		flow.getScript();

		final String actual = flow.applyResource(tuplesJSONFileName);
		final ArrayNode array = objectMapper2.readValue(actual, ArrayNode.class);
		final String finalActual = objectMapper2.writeValueAsString(array);

		final ArrayNode expectedArray = objectMapper2.readValue(expected, ArrayNode.class);
		final String finalExpected = objectMapper2.writeValueAsString(expectedArray);

		Assert.assertEquals(finalExpected.length(), finalActual.length());
		
		//System.out.println(finalActual);

	}


}
