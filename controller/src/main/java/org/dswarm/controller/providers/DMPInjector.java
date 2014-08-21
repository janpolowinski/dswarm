package org.dswarm.controller.providers;

import java.util.concurrent.atomic.AtomicReference;

import com.google.common.base.Optional;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.typesafe.config.Config;

import org.dswarm.controller.guice.DMPModule;
import org.dswarm.controller.guice.DMPServletModule;
import org.dswarm.init.ConfigModule;
import org.dswarm.init.LoggingConfigurator;
import org.dswarm.persistence.JacksonObjectMapperModule;
import org.dswarm.persistence.JpaHibernateModule;
import org.dswarm.persistence.PersistenceModule;

/**
 * The Guice injector for the backend API. Register here all Guice configuration that should be recognized when the backend API is
 * running.
 * 
 * @author phorn
 */
public class DMPInjector extends GuiceServletContextListener {

	private static final AtomicReference<Optional<Injector>> INJECTOR_REF = new AtomicReference<>(Optional.<Injector>absent());

	public DMPInjector() {
		this(Optional.<Injector>absent());
	}

	public DMPInjector(final Optional<Injector> injectorOptional) {
		Optional<Injector> prev;
		do {
			prev = INJECTOR_REF.get();
		} while (!INJECTOR_REF.compareAndSet(prev, injectorOptional));
	}

	@Override
	protected Injector getInjector() {
		return getOrDefault();
	}

	public Config getConfig() {
		return getInjector().getInstance(Config.class);
	}

	static Injector getOrDefault() {
		Optional<Injector> prev, newInjector;
		boolean changed = true;
		do {
			prev = INJECTOR_REF.get();
			if (!prev.isPresent()) {
				newInjector = Optional.of(defaultInjector());
				changed = INJECTOR_REF.compareAndSet(prev, newInjector);
			} else {
				newInjector = prev;
			}
		} while (!changed);

		return newInjector.get();
	}

	private static Injector defaultInjector() {
		final ConfigModule configModule = new ConfigModule();
		final Injector configInjector = Guice.createInjector(configModule);

		final Config config = configInjector.getInstance(Config.class);
		LoggingConfigurator.configureFrom(config);

		return configInjector.createChildInjector(
				new JpaHibernateModule(configInjector),
				new JacksonObjectMapperModule(),
				new PersistenceModule(),
				new DMPModule(),
				new DMPServletModule());
	}
}
