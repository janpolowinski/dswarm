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
package org.dswarm.persistence.service.schema.test.internalmodel;

import org.dswarm.persistence.model.schema.NameSpacePrefixRegistry;
import org.dswarm.persistence.model.schema.Schema;

public class BiboDocumentSchemaBuilder extends SchemaBuilder {

	@Override
	public Schema buildSchema() {

		final AttributePathBuilder builder = new AttributePathBuilder();

		final Schema tempSchema = new Schema();

		tempSchema.setRecordClass(builder.createClass(NameSpacePrefixRegistry.BIBO + "Document", "Document"));

		/*
		 * // Example of how to use the normal API of the attribute path builder
		 * biboDocumentSchema.addAttributePath(builder.start().add(DC + "creator").add(FOAF + "first_name").getPath());
		 */

		// basic properties used in DINI-AG Titeldaten recommendations
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dc:title"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("rda:otherTitleInformation"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dcterms:alternative"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("bibo:shortTitle"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dcterms:creator"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dc:creator"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dcterms:contributor"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dc:contributor"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("rda:publicationStatement"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("rda:placeOfPublication"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dc:publisher"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dcterms:issued"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("owl:sameAs"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("umbel:isLike"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("umbel:isLike"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("bibo:issn"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("bibo:eissn"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("bibo:lccn"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("bibo:oclcnum"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("bibo:isbn"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("rdf:type"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dcterms:medium"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dcterms:hasPart"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dcterms:isPartOf"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dcterms:hasVersion"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dcterms:isFormatOf"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("rda:precededBy"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("rda:succeededBy"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dcterms:language"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("isbd:1053"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("bibo:edition"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dcterms:bibliographicCitation"));

		// extra (added to have some details on creator/contributor resources):
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dcterms:creator/rdf:type"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dcterms:creator/foaf:familyName"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dcterms:creator/foaf:givenName"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dcterms:contributor/rdf:type"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dcterms:contributor/foaf:familyName"));
		tempSchema.addAttributePathInstance(builder.parseAsAttributePathInstance("dcterms:contributor/foaf:givenName"));

		// This can be generated from an excel file Jan curates

		// store all parsed paths as an overview
		prefixPaths = builder.getPrefixPaths();

		final Schema persistentSchema = createSchema("bibo:Document-Schema (KIM-Titeldaten)", tempSchema.getUniqueAttributePathInstances(),
				tempSchema.getRecordClass());

		return persistentSchema;
	}

}
