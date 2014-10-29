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
package org.dswarm.persistence.model.schema;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dswarm.init.DMPException;
import org.dswarm.persistence.model.BasicDMPJPAObject;
import org.dswarm.persistence.util.DMPPersistenceUtil;

/**
 * A data schema is a collection of {@link AttributePath}s and a record class ({@link Clasz}) and optionally it contains a content
 * schema ({@link ContentSchema}).
 *
 * @author tgaengler
 */
@XmlRootElement
@Entity
// @Cacheable(true)
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "DATA_SCHEMA")
public class Schema extends BasicDMPJPAObject {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private static final Logger LOG								= LoggerFactory.getLogger(Schema.class);

	/**
	 * All attribute path (instances) of the schema.
	 */
	// @ManyToMany(mappedBy = "schemas", fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.MERGE,
	// CascadeType.PERSIST, CascadeType.REFRESH })
	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "SCHEMAS_SCHEMA_ATTRIBUTE_PATHS_INSTANCES", joinColumns = { @JoinColumn(name = "SCHEMA_ID", referencedColumnName = "ID") }, inverseJoinColumns = { @JoinColumn(name = "SCHEMA_ATTRIBUTE_PATH_INSTANCE_ID", referencedColumnName = "ID") })
	@JsonIgnore
	private Set<SchemaAttributePathInstance>	attributePathInstances;

	/**
	 * All attribute path (instances) of the schema in their correct order.
	 */
	@Transient
	private List<SchemaAttributePathInstance>		orderedAttributePathInstances;

	/**
	 * All attribute path (instances) of the schema in their correct order as a Json array.
	 */
	@Transient
	private ArrayNode				orderedAttributePathInstancesJson;

	/**
	 * true if the attribute paths were already initialized
	 */
	@Transient
	private boolean					isOrderedAttributePathInstancesInitialized;

	/**
	 * A Json string of all attribute path (instances) of this schema.
	 * This is a serialization of {@link #orderedAttributePathInstancesJson}
	 */
	@JsonIgnore
	@Lob
	@Access(AccessType.FIELD)
	@Column(name = "ATTRIBUTE_PATHS", columnDefinition = "VARCHAR(4000)", length = 4000)
	private String					attributePathInstancesJsonString;

	/**
	 * The record class of the schema.
	 */
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "RECORD_CLASS")
	@XmlElement(name = "record_class")
	private Clasz				recordClass;

	/**
	 * The content schema of the schema.
	 */
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "CONTENT_SCHEMA")
	@XmlElement(name = "content_schema")
	private ContentSchema		contentSchema;


	public Schema() {
	}

	/**
	 * @return an ordered list of all attribute path (instances).
	 */
	@XmlElement(name = "attribute_paths")
	public List<SchemaAttributePathInstance> getAttributePathInstances() {
		tryInitializeOrderedAttributePathInstances();
		return orderedAttributePathInstances;
	}

	/**
	 * Sets all attribute path (instances) of the schema.
	 *
	 * @param attributePathsArg all attribute paths instances of the schema
	 */
	@XmlElement(name = "attribute_paths")
	public void setAttributePathInstances(final Collection<SchemaAttributePathInstance> attributePathsArg) {
		if (attributePathsArg == null) {
			removeAllAttributePathInstances();
		} else {
			setAllAttributePathInstances(attributePathsArg);
		}
	}

	/**
	 * Gets all attribute path (instances) of the schema.
	 *
	 * @return all attribute paths instances of the schema
	 */
	@JsonIgnore
	public Set<SchemaAttributePathInstance> getUniqueAttributePathInstances() {
		return attributePathInstances;
	}

	/**
	 * Gets the attribute path (instance) for the given attribute path identifier.
	 *
	 * @param id an attribute path identifier
	 * @return that matched attribute path (instance) or null
	 */
	public SchemaAttributePathInstance getAttributePathInstance(final Long id) {
		Preconditions.checkNotNull(id);

		if (attributePathInstances != null) {
			for (final SchemaAttributePathInstance attributePath : attributePathInstances) {
				if (attributePath.getId().equals(id)) {
					return attributePath;
				}
			}
		}

		return null;
	}

	/**
	 * Adds a new attribute path (instance) to the collection of attribute path (instances) of this schema.<br>
	 *
	 * @param attributePath a new attribute path
	 */
	public void addAttributePathInstance(final SchemaAttributePathInstance attributePath) {
		
		Preconditions.checkNotNull(attributePath);

		ensureAttributePathInstances();
		ensureInitializedOrderedAttributePathInstances();
		
		// TODO check if equals method works for SAPIs, otherwise the usage of contains may fail here

		if (!attributePathInstances.contains(attributePath)) {

			attributePathInstances.add(attributePath);
			orderedAttributePathInstances.add(attributePath);

			refreshAttributePathInstancesString();
		}
	}

	/**
	 * Adds a new attribute path (instance) at the given index, overwriting any existing attribute path (instance).
	 *
	 * @param attributePath the attribute path instance to add
	 * @param atIndex		the index at which to add
	 */
	public void addAttributePathInstance(final SchemaAttributePathInstance attributePath, final int atIndex) {
		Preconditions.checkNotNull(attributePath);
		Preconditions.checkArgument(atIndex >= 0, "insertion index must be positive");
		Preconditions.checkArgument(atIndex <= orderedAttributePathInstances.size(), "insertion index must not be greater than %s", orderedAttributePathInstances.size());

		ensureAttributePathInstances();
		ensureInitializedOrderedAttributePathInstances();

		if (!attributePath.equals(orderedAttributePathInstances.get(atIndex))) {
			orderedAttributePathInstances.add(atIndex, attributePath);
			attributePathInstances.add(attributePath);
			refreshAttributePathInstancesString();
		}
	}

	/**
	 * Removes an existing attribute path (instance) from the collection of attribute path (instances) of this export schema.<br>
	 * Created by: tgaengler
	 *
	 * @param attributePath an existing attribute path instance that should be removed
	 */
	public void removeAttributePathInstance(final AttributePathInstance attributePath) {
		if (attributePath != null && attributePathInstances != null) {
			final boolean isRemoved = attributePathInstances.remove(attributePath);
			if (isRemoved && orderedAttributePathInstances != null) {
				orderedAttributePathInstances.remove(attributePath);
			}
		}
	}

	/**
	 * Removes an attribute path (instance) if it occurs at a specific index.
	 *
	 * @param attributePath the attribute path instance to remove
	 * @param atIndex       the index from which to remove
	 * @return true if the attribute path instance could be removed, false otherwise.
	 */
	public boolean removeAttributePathInstance(final AttributePath attributePath, final int atIndex) {
		Preconditions.checkNotNull(attributePath);
		Preconditions.checkArgument(atIndex >= 0, "deletion index must be positive");
		Preconditions.checkArgument(atIndex < orderedAttributePathInstances.size(), "deletion index must be less than {}", orderedAttributePathInstances.size());

		if (orderedAttributePathInstances != null) {
			if (orderedAttributePathInstances.get(atIndex).equals(attributePath)) {
				orderedAttributePathInstances.remove(atIndex);
				if (attributePathInstances != null) {
					attributePathInstances.remove(attributePath);
				}
				return true;
			}
		} else {
			if (attributePathInstances != null) {
				return attributePathInstances.remove(attributePath);
			}
		}
		return false;
	}

	/**
	 * Gets the record class of the schema.
	 *
	 * @return the record class of the schema
	 */
	public Clasz getRecordClass() {

		return recordClass;
	}

	/**
	 * Sets the record class of the schema.
	 *
	 * @param recordClassArg a new record class
	 */
	public void setRecordClass(final Clasz recordClassArg) {

		recordClass = recordClassArg;
	}

	/**
	 * Gets the content schema of the schema.
	 *
	 * @return the content schema of the schema
	 */
	public ContentSchema getContentSchema() {

		return contentSchema;
	}

	/**
	 * Sets the content schema of the schema.
	 *
	 * @param contentSchemaArg a new content schema
	 */
	public void setContentSchema(final ContentSchema contentSchemaArg) {

		contentSchema = contentSchemaArg;
	}

	@Override
	public boolean equals(final Object obj) {

		return Schema.class.isInstance(obj) && super.equals(obj);

	}

	@Override
	public boolean completeEquals(final Object obj) {

		return Schema.class.isInstance(obj) && super.completeEquals(obj)
				&& DMPPersistenceUtil.getSchemaAttributePathInstanceUtils().completeEquals(((Schema) obj).getUniqueAttributePathInstances(), getUniqueAttributePathInstances())
				&& DMPPersistenceUtil.getClaszUtils().completeEquals(((Schema) obj).getRecordClass(), getRecordClass())
				&& DMPPersistenceUtil.getContentSchemaUtils().completeEquals(((Schema) obj).getContentSchema(), getContentSchema());
	}

	private void ensureAttributePathInstances() {
		if (attributePathInstances == null) {
			attributePathInstances = Sets.newCopyOnWriteArraySet();
		}
	}

	private void ensureOrderedAttributePathInstances() {
		if (orderedAttributePathInstances == null) {
			orderedAttributePathInstances = Lists.newLinkedList();
		}
	}

	@JsonIgnore
	private void setAllAttributePathInstances(final Collection<SchemaAttributePathInstance> attributePathsArg) {
		ensureOrderedAttributePathInstances();

		if (!DMPPersistenceUtil.getSchemaAttributePathInstanceUtils().completeEquals(orderedAttributePathInstances, attributePathsArg)) {
			ensureAttributePathInstances();

			attributePathInstances.clear();
			orderedAttributePathInstances.clear();

			for (final SchemaAttributePathInstance newAttributePath : attributePathsArg) {
				orderedAttributePathInstances.add(newAttributePath);
				attributePathInstances.add(newAttributePath);
			}
		}

		refreshAttributePathInstancesString();
	}

	private void removeAllAttributePathInstances() {
		if (orderedAttributePathInstances != null) {
			if (attributePathInstances != null) {
				attributePathInstances.clear();
			}
			orderedAttributePathInstances.clear();
			refreshAttributePathInstancesString();
		}
	}

	private void ensureInitializedOrderedAttributePathInstances() {
		if (orderedAttributePathInstances == null) {
			final Optional<List<SchemaAttributePathInstance>> paths = initializedAttributePathInstances(true);
			orderedAttributePathInstances = paths.or(Lists.<SchemaAttributePathInstance>newLinkedList());
		}
	}

	private void tryInitializeOrderedAttributePathInstances() {
		final Optional<List<SchemaAttributePathInstance>> paths = initializedAttributePathInstances(false);
		orderedAttributePathInstances = paths.orNull();
	}

	private Optional<List<SchemaAttributePathInstance>> initializedAttributePathInstances(final boolean fromScratch) {
		if (orderedAttributePathInstancesJson == null && !isOrderedAttributePathInstancesInitialized) {

			if (attributePathInstancesJsonString == null) {
				Schema.LOG.debug("attribute paths JSON is null for {}", getId());

				if (fromScratch) {
					orderedAttributePathInstancesJson = new ArrayNode(DMPPersistenceUtil.getJSONFactory());
					orderedAttributePathInstances = Lists.newLinkedList();

					isOrderedAttributePathInstancesInitialized = true;
				}

				return Optional.fromNullable(orderedAttributePathInstances);
			}

			try {
				orderedAttributePathInstances = Lists.newLinkedList();
				orderedAttributePathInstancesJson = DMPPersistenceUtil.getJSONArray(attributePathInstancesJsonString);

				if (orderedAttributePathInstancesJson != null) {

					for (final JsonNode attributePathIdNode : orderedAttributePathInstancesJson) {
						final SchemaAttributePathInstance attributePath = getAttributePathInstance(attributePathIdNode.longValue());
						if (attributePath != null) {
							orderedAttributePathInstances.add(attributePath);
						}
					}
				}
			} catch (final DMPException e) {
				Schema.LOG.debug("couldn't parse attribute path JSON for attribute path '" + getId() + "'", e);
			}
			isOrderedAttributePathInstancesInitialized = true;
		}

		return Optional.fromNullable(orderedAttributePathInstances);
	}

	private void refreshAttributePathInstancesString() {
		if (orderedAttributePathInstances != null) {
			orderedAttributePathInstancesJson = new ArrayNode(DMPPersistenceUtil.getJSONFactory());
			for (final SchemaAttributePathInstance attributePath : orderedAttributePathInstances) {
				orderedAttributePathInstancesJson.add(attributePath.getId());
			}
		}

		if (orderedAttributePathInstancesJson != null && orderedAttributePathInstancesJson.size() > 0) {
			attributePathInstancesJsonString = orderedAttributePathInstancesJson.toString();
		} else {
			attributePathInstancesJsonString = null;
		}
	}
}
