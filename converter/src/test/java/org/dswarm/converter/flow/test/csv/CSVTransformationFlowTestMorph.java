package org.dswarm.converter.flow.test.csv;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import com.google.common.base.Optional;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.io.Resources;
import com.google.inject.Provider;
import org.culturegraph.mf.formeta.formatter.FormatterStyle;
import org.culturegraph.mf.morph.Metamorph;
import org.culturegraph.mf.stream.converter.FormetaEncoder;
import org.culturegraph.mf.stream.converter.xml.SimpleXmlEncoder;
import org.culturegraph.mf.stream.reader.CsvReader;
import org.culturegraph.mf.stream.sink.ObjectJavaIoWriter;
import org.culturegraph.mf.stream.source.FileOpener;
import org.junit.Assert;
import org.junit.Test;

import org.dswarm.converter.GuicedTest;
import org.dswarm.converter.flow.TransformationFlow;
import org.dswarm.converter.mf.stream.GDMEncoder;
import org.dswarm.converter.mf.stream.GDMModelReceiver;
import org.dswarm.converter.mf.stream.reader.JsonNodeReader;
import org.dswarm.converter.pipe.StreamJsonCollapser;
import org.dswarm.converter.pipe.StreamUnflattener;
import org.dswarm.init.util.DMPStatics;
import org.dswarm.persistence.model.internal.gdm.GDMModel;
import org.dswarm.persistence.model.resource.DataModel;
import org.dswarm.persistence.model.types.Tuple;
import org.dswarm.persistence.service.InternalModelServiceFactory;
import org.dswarm.persistence.util.DMPPersistenceUtil;


/**
 * @author polowins
 * 
 * Test for sketching a morph script to produce nested, structured output data.
 * 
 */
public class CSVTransformationFlowTestMorph extends GuicedTest {
	
	private static final Logger							LOG	= LoggerFactory.getLogger(CSVTransformationFlowTestMorph.class);
	
	public final static String MORPH_DEFINITION_STRUCTURED_OUTPUT = "dd-700/structured-output.xml";
	public final static String MORPH_DEFINITION_SUBSTRING = "dd-700/substring-replace.xml";

	/**
	 * Test for sketching a morph script to produce nested, structured output data.
	 * 
	 * @throws Exception
	 */
	//@Test
	public void testConstructionOfStructuredOutputWithMorph() throws Exception {

		//executeCSVMorphWithTuples("dd-700/structured-output.xml", "dd-700/test_book_with_author_data_flat.tuples.json");
		//testCSVMorphWithTuples("dd-700/test_book_with_author_data.result.json", "dd-700/structured-output.xml", "dd-700/test_book_with_author_data_flat.tuples.json");
	}
	
	/**
	 * Test this test with some substring replacement morph.
	 * 
	 * @throws Exception
	 */
	/*
	//@Test
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
	
	private void executeCSVMorphWithTuples(final String morphXMLFileName, final String tuplesJSONFileName)
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

	
	*/
	
	/**
	 * Test building sub-entities using standard metafacture formeta format as output
	 * 
	 * @throws Exception
	 */
	//@Test
	public void testComplexMorphJson2Formeta() throws Exception {
		
		
		final JsonNodeReader jsonNodeReader = new JsonNodeReader();
		
		final Metamorph metamorph = new Metamorph(Resources.getResource(MORPH_DEFINITION_STRUCTURED_OUTPUT).getPath());
		//final RdfMacroPipe rdfMacroPipe = new RdfMacroPipe();
		final SimpleXmlEncoder xmlEncoder = new SimpleXmlEncoder();	
		final FormetaEncoder formetaEncoder = new FormetaEncoder();	
		formetaEncoder.setStyle(FormatterStyle.MULTILINE);
		
		//metamorph.setErrorHandler(new MetamorphErrorHandlerImpl());
		
		//xmlEncoder.setRootTag(ROOT_TAG);
		//xmlEncoder.setRecordTag(RECORD_TAG);
		//xmlEncoder.setNamespaceFile(NAMESPACE_MAPPING);


		jsonNodeReader
		.setReceiver(metamorph)
		.setReceiver(formetaEncoder)
		;	
		
		final StringWriter stringWriter = new StringWriter();
		final ObjectJavaIoWriter<String> streamWriter = new ObjectJavaIoWriter<String>(stringWriter);
		
		xmlEncoder.setReceiver(streamWriter);
		formetaEncoder.setReceiver(streamWriter);
		
		jsonNodeReader.process(getTupleList(DMPPersistenceUtil.getResourceAsString("dd-700/test_book_with_author_data_flat.tuples.json")).iterator());
		
		String result = stringWriter.toString();
		System.out.println(result);		
	}
	
	/**
	 * Test building sub-entities using GDM Json as output format again
	 * 
	 * @throws Exception
	 */
	@Test
	public void testComplexMorphJson2Json() throws Exception {
		
		
		final JsonNodeReader jsonNodeReader = new JsonNodeReader();
		
		final Metamorph metamorph = new Metamorph(Resources.getResource(MORPH_DEFINITION_STRUCTURED_OUTPUT).getPath());
		
		
		final StreamUnflattener unflattener = new StreamUnflattener("", DMPStatics.ATTRIBUTE_DELIMITER);
		final StreamJsonCollapser collapser = new StreamJsonCollapser();
		
		Optional<DataModel> dataModel = Optional.absent();
		
		final GDMEncoder converter = new GDMEncoder(dataModel);
		final GDMModelReceiver writer = new GDMModelReceiver();

		jsonNodeReader
		.setReceiver(metamorph)
		//.setReceiver(unflattener)
		//.setReceiver(collapser)
		.setReceiver(converter)
		.setReceiver(writer);
		;	
		
		
//		final StringWriter stringWriter = new StringWriter();
//		final ObjectJavaIoWriter<String> streamWriter = new ObjectJavaIoWriter<String>(stringWriter);
//
//		formetaEncoder.setReceiver(streamWriter);
//		
		jsonNodeReader.process(getTupleList(DMPPersistenceUtil.getResourceAsString("dd-700/test_2_book_with_author_data_flat.tuples.json")).iterator());
		jsonNodeReader.closeStream();
//		
//		String result = stringWriter.toString();
//		System.out.println(result);		
		
		
		final ImmutableList<GDMModel> gdmModels = writer.getCollection();
		
		for (GDMModel gdmModel : gdmModels) {
			System.out.println(gdmModel);
		}

	}
	
	
	private List<Tuple<String, JsonNode>> getTupleList(String jsonRecordString){
		
		// TODO: convert JSON string to Iterator with tuples of string + JsonNode pairs
		List<Tuple<String, JsonNode>> tuplesList = null;

		try {

			tuplesList = DMPPersistenceUtil.getJSONObjectMapper().readValue(jsonRecordString, new TypeReference<List<Tuple<String, JsonNode>>>() {

			});
		} catch (final JsonParseException e) {

			LOG.debug("couldn't parse the transformation result tuples to a JSON string");
		} catch (final JsonMappingException e) {

			LOG.debug("couldn't map the transformation result tuples to a JSON string");
		} catch (final IOException e) {

			LOG.debug("something went wrong while processing the transformation result tuples to a JSON string");
		}

		if (tuplesList == null) {

			LOG.debug("couldn't process the transformation result tuples to a JSON string");

			return null;
		}
		
		return tuplesList;
		
	}
	

}
