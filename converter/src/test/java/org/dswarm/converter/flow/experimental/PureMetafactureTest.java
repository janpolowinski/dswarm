package org.dswarm.converter.flow.experimental;

import java.io.File;
import java.io.StringWriter;

import com.google.common.io.Resources;
import org.culturegraph.mf.formeta.formatter.FormatterStyle;
import org.culturegraph.mf.morph.Metamorph;
import org.culturegraph.mf.stream.converter.FormetaEncoder;
import org.culturegraph.mf.stream.converter.xml.SimpleXmlEncoder;
import org.culturegraph.mf.stream.reader.CsvReader;
import org.culturegraph.mf.stream.sink.ObjectJavaIoWriter;
import org.culturegraph.mf.stream.source.FileOpener;
import org.junit.Test;


/**
 * @author polowins
 * 
 * Experimental Zone for sketching a morph script to produce nested, structured output data.
 * 
 */
public class PureMetafactureTest {
	
	public final static String MORPH_DEFINITION_STRUCTURED_OUTPUT = "dd-700/structured-output.xml";
	public final static String MORPH_DEFINITION_SUBSTRING = "dd-700/substring-replace.xml";
	public final static String EXAMPLE_DATA_CSV = "dd-700/test_book_with_author_data_flat.csv";

	/**
	 * Test sub-entities with standard metafacture means and encode as XML
	 * 
	 * @throws Exception
	 */
	@Test 
	public void testComplexMorphXML() throws Exception {
		
		final File file = new File(Resources.getResource(EXAMPLE_DATA_CSV).getPath());
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
	
	/**
	 * Test sub-entities with standard metafacture means and encode as formeta
	 * 
	 * @throws Exception
	 */
	@Test
	public void testComplexMorphFormeta() throws Exception {
		
		final File file = new File(Resources.getResource(EXAMPLE_DATA_CSV).getPath());
		final FileOpener opener = new FileOpener();

		final CsvReader csvReader = new CsvReader(",");
		csvReader.setHasHeader(true);
		
		final Metamorph metamorph = new Metamorph(Resources.getResource(MORPH_DEFINITION_STRUCTURED_OUTPUT).getPath());
		final FormetaEncoder formetaEncoder = new FormetaEncoder();	
		formetaEncoder.setStyle(FormatterStyle.MULTILINE);
		

		opener
		.setReceiver(csvReader)
		.setReceiver(metamorph)
		.setReceiver(formetaEncoder)
		;	
		
		final StringWriter stringWriter = new StringWriter();
		final ObjectJavaIoWriter<String> streamWriter = new ObjectJavaIoWriter<String>(stringWriter);

		formetaEncoder.setReceiver(streamWriter);
		
		opener.process(file.getAbsolutePath());

		String result = stringWriter.toString();
		System.out.println(result);		
	}
	
	

}
