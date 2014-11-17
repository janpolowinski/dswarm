package org.dswarm.converter.mf.stream;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nullable;

import org.hibernate.internal.jaxb.mapping.hbm.SubEntityElement;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.culturegraph.mf.exceptions.MetafactureException;
import org.culturegraph.mf.framework.DefaultStreamPipe;
import org.culturegraph.mf.framework.ObjectReceiver;
import org.culturegraph.mf.framework.StreamReceiver;
import org.culturegraph.mf.framework.annotations.Description;
import org.culturegraph.mf.framework.annotations.In;
import org.culturegraph.mf.framework.annotations.Out;

import org.dswarm.graph.json.LiteralNode;
import org.dswarm.graph.json.Model;
import org.dswarm.graph.json.Node;
import org.dswarm.graph.json.Predicate;
import org.dswarm.graph.json.Resource;
import org.dswarm.graph.json.ResourceNode;
import org.dswarm.graph.json.Statement;
import org.dswarm.persistence.model.internal.gdm.GDMModel;
import org.dswarm.persistence.model.resource.DataModel;
import org.dswarm.persistence.model.resource.utils.DataModelUtils;
import org.dswarm.persistence.util.GDMUtil;

/**
 * TODO: copied from GDMEncoder. Reuse code from GDMEncoder if both classes should coexist in future
 * Converts records to a GDM Model, being aware of nested entities.
 * 
 * @author polowins
 * @author tgaengler
 * @author phorn
 */
@Description("converts records to a GDM Model")
@In(StreamReceiver.class)
@Out(GDMModel.class)
public final class GDMEncoderEntityAware extends DefaultStreamPipe<ObjectReceiver<GDMModel>> {

	private final Model						internalGDMModel;
	private String							recordId;
	private Resource						recordResource;
	private ResourceNode					recordNode;
	
	private String							currentId;
	private Resource						currentEntityResource;
	private ResourceNode					currentEntityNode;
	
	// private Stack<Tuple<Node, Predicate>> entityStack;
	private Stack<Resource> 				entityStack;
	// private final Stack<String> elementURIStack; // TODO use stack when statements for deeper hierarchy levels are possible

	// not used: private ResourceNode recordType;

	private final Optional<DataModel>		dataModel;
	private final Optional<String>			dataModelUri;

	private String							recordTypeUri;

	private final Map<String, Predicate>	predicates		= Maps.newHashMap();
	private final Map<String, AtomicLong>	valueCounter	= Maps.newHashMap();
	private final Map<String, String>		uris			= Maps.newHashMap();

	public GDMEncoderEntityAware(final Optional<DataModel> dataModel) {

		super();

		this.dataModel = dataModel;
		dataModelUri = init(dataModel);

		// init
		entityStack = new Stack<Resource>();
		// elementURIStack = new Stack<>(); // TODO use stack when statements for deeper hierarchy levels are possible
		internalGDMModel = new Model();

	}

	@Override
	public void startRecord(final String identifier) {

		System.out.println("in start record with: identifier = '" + identifier + "'");

		assert !isClosed();

		recordId = isValidUri(identifier) ? identifier : mintRecordUri(identifier);

		recordResource = new Resource(recordId);
		recordNode = new ResourceNode(recordId);
		
		currentEntityResource = recordResource;
		currentEntityNode = recordNode;

	}

	@Override
	public void endRecord() {
		
		System.out.println("in end record");

		assert !isClosed();

		internalGDMModel.addResource(recordResource);

		// write triples
		final GDMModel gdmModel;

		if (recordTypeUri == null) {

			gdmModel = new GDMModel(internalGDMModel, recordId);
			
		} else {

			gdmModel = new GDMModel(internalGDMModel, recordId, recordTypeUri);
			
		}

		getReceiver().process(gdmModel);
	}

	// TODO multiple times nested entities not yet supported (only record entity with one subentity)
	@Override
	public void startEntity(final String name) {

		System.out.println("in start entity with name = '" + name + "'");
		
		System.out.println("current resource " + currentEntityResource.getUri() + " current resource node " + currentEntityNode.getUri());

		assert !isClosed();
		
		final Predicate attributeProperty = getPredicate(name);
		
		currentId = "http://example.org/some/Person";
		
		final Resource subEntityResource = new Resource(currentId);
		final ResourceNode subEntityNode = new ResourceNode(subEntityResource);

		// add the new entity to the parent entity
		addStatement(currentEntityNode, attributeProperty, subEntityNode);
		
		currentEntityResource = subEntityResource;
		currentEntityNode = subEntityNode;
		
		final ResourceNode typeResource = new ResourceNode("http://foaf.de/Person");
		addStatement(currentEntityNode, getPredicate(GDMUtil.RDF_type), typeResource);
		
		
		/*
		// test creating a subentity manually
		final Resource subSubEntityResource = new Resource("http://foaf.de/some/Place");
		final ResourceNode subSubEntityNode = new ResourceNode(subSubEntityResource);
		final ResourceNode typeResourcePlace = new ResourceNode("http://foaf.de/Place");
		subSubEntityResource.addStatement(subSubEntityNode, getPredicate(GDMUtil.RDF_type), typeResourcePlace);
		subEntityResource.addStatement(subEntityNode, getPredicate("bornIn"), subSubEntityNode);
		
		printStatements(subSubEntityResource);
		*/
		
		
		//currentEntityResource.addStatement(currentEntityNode, getPredicate(GDMUtil.RDF_type), new LiteralNode("test"));
		
		System.out.println("current resource " + currentEntityResource.getUri() + " current resource node " + currentEntityNode.getUri());

	}

	// TODO multiple times nested entities not yet supported (only record entity with one subentity)
	@Override
	public void endEntity() {

		System.out.println("in end entity");
		
		System.out.println("current resource " + currentEntityResource.getUri() + " current resource node " + currentEntityNode.getUri());

		assert !isClosed();
		
		internalGDMModel.addResource(currentEntityResource);
		
		currentEntityResource = recordResource;
		currentEntityNode = recordNode;
		currentId = recordId;
		
		System.out.println("current resource " + currentEntityResource.getUri() + " current resource node " + currentEntityNode.getUri());

	}

	@Override
	public void literal(final String name, final String value) {

		System.out.println("in literal with name = '" + name + "' :: value = '" + value + "'");

		System.out.println("current resource " + currentEntityResource.getUri() + " current resource node " + currentEntityNode.getUri());
		
		assert !isClosed();

		// create triple
		// name = predicate
		// value = literal or object -> no. now handled by entities
		// TODO: only literals atm, i.e., how to determine other resources? -> entities
		// ==> @phorn proposed to utilise "<" ">" to identify resource ids (uris)
		if (name == null) {

			return;
		}

		final String propertyUri;

		if (isValidUri(name)) {

			propertyUri = name;
			
		} else {

			propertyUri = mintUri(dataModelUri.get(), name);
		}

		if (value != null && !value.isEmpty()) {

			final Predicate attributeProperty = getPredicate(propertyUri);
			final LiteralNode literalObject = new LiteralNode(value);

			if (null != currentEntityResource) {

				// TODO: this is only a HOTFIX for creating resources from resource type uris

				if (!GDMUtil.RDF_type.equals(propertyUri)) {

					// recordResource.addProperty(attributeProperty, value);
					//currentEntityResource.addStatement(currentEntityNode, attributeProperty, literalObject);
					addStatement(currentEntityNode, attributeProperty, literalObject);
				} else {

					// check, whether value is really a URI
					if (isValidUri(value)) {

						final ResourceNode typeResource = new ResourceNode(value);// ResourceFactory.createResource(value);

						// recordResource.addStatement(entityNode, attributeProperty, typeResource);
						addStatement(currentEntityNode, attributeProperty, typeResource);

						//recordTypeUri = value; // TODO not only set record type here, generalize!
					} else {

						// recordResource.addStatement(entityNode, attributeProperty, literalObject);
						addStatement(currentEntityNode, attributeProperty, literalObject);
					}
				}
			} else {

				throw new MetafactureException("couldn't get a resource for adding this property");
			}
		}
		
				System.out.println("current resource " + currentEntityResource.getUri() + " current resource node " + currentEntityNode.getUri());
				
				printStatements(currentEntityResource);

	}

	private void printStatements(Resource resource) {
		Set<Statement> statements = resource.getStatements();
		
		for (Statement statement : statements) {
			System.out.println(statement);
		}
	}

	private Optional<String> init(final Optional<DataModel> dataModel) {

		if (!dataModel.isPresent()) {

			return Optional.fromNullable(StringUtils.stripEnd(DataModelUtils.determineDataModelSchemaBaseURI(null), "#"));
		}

		return dataModel.transform(new Function<DataModel, String>() {

			@Override
			public String apply(final DataModel dm) {
				return StringUtils.stripEnd(DataModelUtils.determineDataModelSchemaBaseURI(dm), "#");
			}
		});
	}

	private String mintDataModelTermUri(@Nullable final String uri, @Nullable final String localName) {

		final boolean canUseLocalName = !Strings.isNullOrEmpty(localName);

		if (Strings.isNullOrEmpty(uri)) {

			if (dataModelUri.isPresent()) {

				if (canUseLocalName) {

					return mintUri(dataModelUri.get(), localName);
				} else {

					return dataModelUri.get() + "#" + UUID.randomUUID();
				}
			}

			return String.format("http://data.slub-dresden.de/terms/%s", UUID.randomUUID());
		}

		if (canUseLocalName) {

			return mintUri(uri, localName);
		} else {

			return String.format("http://data.slub-dresden.de/terms/%s", UUID.randomUUID());
		}
	}

	private boolean isValidUri(@Nullable final String identifier) {

		if (identifier != null) {

			try {

				final URI uri = URI.create(identifier);

				return uri != null && uri.getScheme() != null;
			} catch (final IllegalArgumentException e) {

				return false;
			}
		}

		return false;
	}

	private String mintRecordUri(@Nullable final String identifier) {

		if (recordId == null) {

			// mint completely new uri

			final StringBuilder sb = new StringBuilder();

			if (dataModel.isPresent()) {

				// create uri from resource id and configuration id and random uuid

				sb.append("http://data.slub-dresden.de/datamodels/").append(dataModel.get().getId()).append("/records/");
			} else {

				// create uri from random uuid

				sb.append("http://data.slub-dresden.de/records/");
			}

			return sb.append(UUID.randomUUID()).toString();
		}

		// create uri with help of given record id

		final StringBuilder sb = new StringBuilder();

		if (dataModel.isPresent()) {

			// create uri from resource id and configuration id and identifier

			sb.append("http://data.slub-dresden.de/datamodels/").append(dataModel.get().getId()).append("/records/").append(identifier);
		} else {

			// create uri from identifier

			sb.append("http://data.slub-dresden.de/records/").append(identifier);
		}

		return sb.toString();
	}

	/**
	 * Combine an URI from a base URI and a local name.
	 * 
	 * @param baseUri - may end with a hash or slash
	 * @param localName
	 * @return the minted URI as a string
	 */
	private String mintUri(final String baseUri, final String localName) {

		if (baseUri != null && baseUri.endsWith("/")) {

			return baseUri + localName;
		}

		return baseUri + "#" + localName;
	}

	/**
	 * Builds a predicate URI from the predicateId and returns the according Predicate. 
	 * 
	 * @param predicateId
	 * @return the found or newly created predicate
	 */
	private Predicate getPredicate(final String predicateId) {

		final String predicateURI = getURI(predicateId);

		if (!predicates.containsKey(predicateURI)) {

			final Predicate predicate = new Predicate(predicateURI);

			predicates.put(predicateURI, predicate);
		}

		return predicates.get(predicateURI);
	}

	/**
	 * Adds an ordered statement to the current entity resource
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	private void addStatement(final Node subject, final Predicate predicate, final Node object) {

		String key;

		if (subject instanceof ResourceNode) {
			
			key = ((ResourceNode) subject).getUri();
			
		} else {

			key = subject.getId().toString();
		}

		key += "::" + predicate.getUri();

		if (!valueCounter.containsKey(key)) {

			final AtomicLong valueCounterForKey = new AtomicLong(0);
			valueCounter.put(key, valueCounterForKey);
		}

		final Long order = valueCounter.get(key).incrementAndGet();

		currentEntityResource.addStatement(subject, predicate, object, order);
	}

	/**
	 * Builds a URI from the string id and returns the URI.
	 * If is is a not a valid URI a datamodel term URI is minted.
	 * 
	 * @param id
	 * @return the found or newly created ID
	 */
	private String getURI(final String id) {

		if (!uris.containsKey(id)) {

			final String uri = isValidUri(id) ? id : mintDataModelTermUri(null, id);

			uris.put(id, uri);
		}

		return uris.get(id);
	}
}
