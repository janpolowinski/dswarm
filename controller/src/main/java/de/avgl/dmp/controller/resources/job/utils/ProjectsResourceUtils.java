package de.avgl.dmp.controller.resources.job.utils;

import java.util.Set;

import javax.inject.Provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import de.avgl.dmp.controller.DMPControllerException;
import de.avgl.dmp.controller.resources.resource.utils.DataModelsResourceUtils;
import de.avgl.dmp.controller.resources.utils.ExtendedBasicDMPResourceUtils;
import de.avgl.dmp.persistence.model.job.Function;
import de.avgl.dmp.persistence.model.job.Mapping;
import de.avgl.dmp.persistence.model.job.Project;
import de.avgl.dmp.persistence.model.resource.DataModel;
import de.avgl.dmp.persistence.service.job.ProjectService;

/**
 * @author tgaengler
 * @param <POJOCLASSPERSISTENCESERVICE>
 * @param <POJOCLASS>
 * @param <POJOCLASSIDTYPE>
 */
public class ProjectsResourceUtils extends ExtendedBasicDMPResourceUtils<ProjectService, Project> {

	private static final org.apache.log4j.Logger	LOG	= org.apache.log4j.Logger.getLogger(ProjectsResourceUtils.class);

	private final Provider<DataModelsResourceUtils>	dataModelsResourceUtilsProvider;

	private final Provider<MappingsResourceUtils>	mappingsResourceUtilsProvider;

	private final Provider<FunctionsResourceUtils>	functionsResourceUtilsProvider;

	@Inject
	public ProjectsResourceUtils(final Provider<ProjectService> persistenceServiceProviderArg, final Provider<ObjectMapper> objectMapperProviderArg,
			final Provider<DataModelsResourceUtils> dataModelsResourceUtilsProviderArg,
			final Provider<MappingsResourceUtils> mappingsResourceUtilsProviderArg,
			final Provider<FunctionsResourceUtils> functionsResourceUtilsProviderArg) {
		super(Project.class, persistenceServiceProviderArg, objectMapperProviderArg);

		dataModelsResourceUtilsProvider = dataModelsResourceUtilsProviderArg;
		mappingsResourceUtilsProvider = mappingsResourceUtilsProviderArg;
		functionsResourceUtilsProvider = functionsResourceUtilsProviderArg;
	}

	@Override
	public JsonNode replaceRelevantDummyIds(final Project object, final JsonNode jsonNode, final Set<Long> dummyIdCandidates)
			throws DMPControllerException {

		if (checkObject(object, dummyIdCandidates)) {

			return jsonNode;
		}

		super.replaceRelevantDummyIds(object, jsonNode, dummyIdCandidates);

		final DataModel inputDataModel = object.getInputDataModel();

		if (replaceRelevantDummyIdsInDataModel(inputDataModel, jsonNode, dummyIdCandidates)) {

			return jsonNode;
		}

		final DataModel outputDataModel = object.getOutputDataModel();

		if (replaceRelevantDummyIdsInDataModel(outputDataModel, jsonNode, dummyIdCandidates)) {

			return jsonNode;
		}

		final Set<Mapping> mappings = object.getMappings();

		if (mappings != null) {

			for (final Mapping mapping : mappings) {

				if (areDummyIdCandidatesEmpty(dummyIdCandidates)) {

					return jsonNode;
				}

				mappingsResourceUtilsProvider.get().replaceRelevantDummyIds(mapping, jsonNode, dummyIdCandidates);
			}
		}

		final Set<Function> functions = object.getFunctions();

		if (functions != null) {

			for (final Function function : functions) {

				if (areDummyIdCandidatesEmpty(dummyIdCandidates)) {

					return jsonNode;
				}

				functionsResourceUtilsProvider.get().replaceRelevantDummyIds(function, jsonNode, dummyIdCandidates);
			}
		}

		return jsonNode;
	}

	private boolean replaceRelevantDummyIdsInDataModel(final DataModel dataModel, final JsonNode jsonNode, final Set<Long> dummyIdCandidates)
			throws DMPControllerException {

		if (dataModel != null) {

			if (areDummyIdCandidatesEmpty(dummyIdCandidates)) {

				return true;
			}

			dataModelsResourceUtilsProvider.get().replaceRelevantDummyIds(dataModel, jsonNode, dummyIdCandidates);
		}

		return false;
	}
}