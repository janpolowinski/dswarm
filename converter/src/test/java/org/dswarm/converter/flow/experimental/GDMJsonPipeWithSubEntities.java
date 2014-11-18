package org.dswarm.converter.flow.experimental;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.culturegraph.mf.formeta.formatter.FormatterStyle;
import org.culturegraph.mf.morph.Metamorph;
import org.culturegraph.mf.stream.converter.FormetaEncoder;
import org.culturegraph.mf.stream.converter.xml.SimpleXmlEncoder;
import org.culturegraph.mf.stream.sink.ObjectJavaIoWriter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dswarm.converter.GuicedTest;
import org.dswarm.converter.mf.stream.GDMEncoderEntityAware;
import org.dswarm.converter.mf.stream.GDMModelReceiver;
import org.dswarm.converter.mf.stream.reader.JsonNodeReader;
import org.dswarm.converter.pipe.StreamJsonCollapser;
import org.dswarm.converter.pipe.StreamUnflattener;
import org.dswarm.init.util.DMPStatics;
import org.dswarm.persistence.model.internal.gdm.GDMModel;
import org.dswarm.persistence.model.resource.DataModel;
import org.dswarm.persistence.model.types.Tuple;
import org.dswarm.persistence.util.DMPPersistenceUtil;


/**
 * @author polowins
 * 
 * Test for sketching a morph script to produce nested, structured output data.
 * 
 */
public class GDMJsonPipeWithSubEntities extends GuicedTest {
	
	private static final Logger	LOG	= LoggerFactory.getLogger(GDMJsonPipeWithSubEntities.class);
	
	public final static String MORPH_DEFINITION_STRUCTURED_OUTPUT = "dd-700/structured-output.xml";
	public final static String MORPH_DEFINITION_SUBSTRING = "dd-700/substring-replace.xml";

	
	/**
	 * Test building sub-entities using standard metafacture formeta format as output
	 * 
	 * @throws Exception
	 */
	@Test
	public void testComplexMorphJson2Formeta() throws Exception {

		final JsonNodeReader jsonNodeReader = new JsonNodeReader();
		
		final Metamorph metamorph = new Metamorph(Resources.getResource(MORPH_DEFINITION_STRUCTURED_OUTPUT).getPath());	
		final FormetaEncoder formetaEncoder = new FormetaEncoder();	
		formetaEncoder.setStyle(FormatterStyle.MULTILINE);

		jsonNodeReader
		.setReceiver(metamorph)
		.setReceiver(formetaEncoder)
		;	
		
		final StringWriter stringWriter = new StringWriter();
		final ObjectJavaIoWriter<String> streamWriter = new ObjectJavaIoWriter<String>(stringWriter);

		formetaEncoder.setReceiver(streamWriter);
		
		jsonNodeReader.process(getTupleList(DMPPersistenceUtil.getResourceAsString("dd-700/test_book_with_author_data_flat.tuples.json")).iterator());
		
		String result = stringWriter.toString();
		System.out.println("Formeta:");	
		System.out.println(result);		
	}
	
	/**
	 * Test building sub-entities using standard metafacture xml format as output
	 * 
	 * @throws Exception
	 */
	@Test
	public void testComplexMorphJson2XML() throws Exception {
		
		final JsonNodeReader jsonNodeReader = new JsonNodeReader();
		
		final Metamorph metamorph = new Metamorph(Resources.getResource(MORPH_DEFINITION_STRUCTURED_OUTPUT).getPath());
		//final RdfMacroPipe rdfMacroPipe = new RdfMacroPipe();
		final SimpleXmlEncoder xmlEncoder = new SimpleXmlEncoder();	
		
		//metamorph.setErrorHandler(new MetamorphErrorHandlerImpl());
		
		//xmlEncoder.setRootTag(ROOT_TAG);
		//xmlEncoder.setRecordTag(RECORD_TAG);
		//xmlEncoder.setNamespaceFile(NAMESPACE_MAPPING);

		jsonNodeReader
		.setReceiver(metamorph)
		.setReceiver(xmlEncoder)
		;	
		
		final StringWriter stringWriter = new StringWriter();
		final ObjectJavaIoWriter<String> streamWriter = new ObjectJavaIoWriter<String>(stringWriter);
		
		xmlEncoder.setReceiver(streamWriter);
		
		jsonNodeReader.process(getTupleList(DMPPersistenceUtil.getResourceAsString("dd-700/test_book_with_author_data_flat.tuples.json")).iterator());
		
		String result = stringWriter.toString();
		System.out.println("XML:");	
		System.out.println(result);		
	}
	
	/**
	 * Test building sub-entities using GDM Json as input and output format
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
		
		final GDMEncoderEntityAware converter = new GDMEncoderEntityAware(dataModel);
		final GDMModelReceiver writer = new GDMModelReceiver();

		jsonNodeReader
		.setReceiver(metamorph)
		//.setReceiver(unflattener)
		//.setReceiver(collapser)
		.setReceiver(converter)
		.setReceiver(writer);
		;	

		jsonNodeReader.process(getTupleList(DMPPersistenceUtil.getResourceAsString("dd-700/test_book_with_author_data_flat.tuples.json")).iterator());
		jsonNodeReader.closeStream();

		
		final ImmutableList<GDMModel> gdmModels = writer.getCollection();
		
		for (GDMModel gdmModel : gdmModels) {
			
			final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).configure(
					SerializationFeature.INDENT_OUTPUT, true);
			
			final String prettyPrintedJson = objectMapper.writeValueAsString(gdmModel.toJSON());
			final String prettyPrintedRawJson = objectMapper.writeValueAsString(gdmModel.toRawJSON());

			System.out.println("GDM JSON (pretty printed):");
			System.out.println(prettyPrintedJson);
			
			System.out.println("RAW JSON (pretty printed):");
			System.out.println(prettyPrintedRawJson);
			
			//System.out.println("JSON:");
			//System.out.println(gdmModel.toJSON());
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
