package de.avgl.dmp.controller.eventbus;

import java.util.List;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.avgl.dmp.converter.DMPConverterException;
import de.avgl.dmp.converter.flow.CSVResourceFlowFactory;
import de.avgl.dmp.converter.flow.CSVSourceResourceTriplesFlow;
import de.avgl.dmp.persistence.model.resource.Configuration;
import de.avgl.dmp.persistence.model.resource.Resource;
import de.avgl.dmp.persistence.services.InternalService;

@Singleton
public class ConverterEventRecorder {

	private InternalService internalService;

	@Inject
	public ConverterEventRecorder(final InternalService internalService, final EventBus eventBus) {

		this.internalService = internalService;
		eventBus.register(this);
	}

	@Subscribe public void convertConfiguration(ConverterEvent event) {
		final Configuration configuration = event.getConfiguration();
		final Resource resource = event.getResource();

		List<org.culturegraph.mf.types.Triple> result = null;
		try {
			final CSVSourceResourceTriplesFlow flow = CSVResourceFlowFactory.fromConfiguration(configuration, CSVSourceResourceTriplesFlow.class);

			final String path = resource.getAttribute("path").asText();
			result = flow.applyFile(path);

		} catch (DMPConverterException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		if (result != null) {
			for (org.culturegraph.mf.types.Triple triple : result) {
				internalService.createObject(resource.getId(), configuration.getId(), triple.getSubject(), triple.getPredicate(), triple.getObject());
			}
		}
	}
}