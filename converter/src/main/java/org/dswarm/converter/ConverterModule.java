package org.dswarm.converter;

import com.google.inject.AbstractModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dswarm.converter.schema.XMLSchemaParser;
import org.dswarm.init.util.DMPUtil;

/**
 * The Guice configuration of the converter module. Interface/classes that are registered here can be utilised for injection.
 * 
 * @author phorn
 */
public class ConverterModule extends AbstractModule {

	private static final Logger	LOG	= LoggerFactory.getLogger(ConverterModule.class);

	@Override
	protected void configure() {

		bind(XMLSchemaParser.class);
	}
}
