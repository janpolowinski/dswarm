package org.dswarm.converter.flow.test.csv;

import java.io.File;
import java.io.StringWriter;

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
import org.dswarm.converter.mf.stream.reader.JsonNodeReader;
import org.dswarm.converter.pipe.StreamJsonCollapser;
import org.dswarm.converter.pipe.StreamUnflattener;
import org.dswarm.init.util.DMPStatics;
import org.dswarm.persistence.service.InternalModelServiceFactory;
import org.dswarm.persistence.util.DMPPersistenceUtil;


/**
 * @author polowins
 * 
 * Test for sketching a morph script to produce nested, structured output data.
 * 
 */
public class PureMetafactureTest {
	
	public final static String MORPH_DEFINITION_STRUCTURED_OUTPUT = "dd-700/structured-output.xml";
	public final static String MORPH_DEFINITION_SUBSTRING = "dd-700/substring-replace.xml";
	public final static String CSV = "dd-700/test_book_with_author_data_flat.csv";

	/**
	 * Test sub-entities with standard metafacture means
	 * 
	 * @throws Exception
	 */
	@Test
	public void testComplexMorph() throws Exception {
		
		final File file = new File(Resources.getResource(CSV).getPath());
		final FileOpener opener = new FileOpener();

		final CsvReader csvReader = new CsvReader(",");
		csvReader.setHasHeader(true);
		
		//final CsvDecoder csvDecoder = new CsvDecoder(",");	
		//final PicaOccurenceRemover occurenceFilter = new PicaOccurenceRemover();
		final Metamorph metamorph = new Metamorph(Resources.getResource(MORPH_DEFINITION_STRUCTURED_OUTPUT).getPath());
		//final RdfMacroPipe rdfMacroPipe = new RdfMacroPipe();
		final SimpleXmlEncoder xmlEncoder = new SimpleXmlEncoder();	
		final FormetaEncoder formetaEncoder = new FormetaEncoder();	
		formetaEncoder.setStyle(FormatterStyle.MULTILINE);
		
		//metamorph.setErrorHandler(new MetamorphErrorHandlerImpl());
		
		//xmlEncoder.setRootTag(ROOT_TAG);
		//xmlEncoder.setRecordTag(RECORD_TAG);
		//xmlEncoder.setNamespaceFile(NAMESPACE_MAPPING);


		opener
		.setReceiver(csvReader)
		//csvDecoder
		//.setReceiver(occurenceFilter)
		.setReceiver(metamorph)
		//.setReceiver(rdfMacroPipe)
		.setReceiver(xmlEncoder)
		//.setReceiver(formetaEncoder)
		;	
		
		final StringWriter stringWriter = new StringWriter();
		final ObjectJavaIoWriter<String> streamWriter = new ObjectJavaIoWriter<String>(stringWriter);
		
		xmlEncoder.setReceiver(streamWriter);
		formetaEncoder.setReceiver(streamWriter);
		
		//csvDecoder.process("some,csv,value");
		opener.process(file.getAbsolutePath());

		String result = stringWriter.toString();
		System.out.println(result);		
	}
	
	

}
