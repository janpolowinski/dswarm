package de.avgl.dmp.controller.resources.test.utils;

import org.junit.Assert;

import de.avgl.dmp.persistence.model.ExtendedBasicDMPJPAObject;
import de.avgl.dmp.persistence.service.ExtendedBasicDMPJPAService;

public class ExtendedBasicDMPResourceTestUtils<POJOCLASSPERSISTENCESERVICE extends ExtendedBasicDMPJPAService<POJOCLASS>, POJOCLASS extends ExtendedBasicDMPJPAObject>
		extends BasicDMPResourceTestUtils<POJOCLASSPERSISTENCESERVICE, POJOCLASS> {

	public ExtendedBasicDMPResourceTestUtils(final String resourceIdentifier, final Class<POJOCLASS> pojoClassArg,
			final Class<POJOCLASSPERSISTENCESERVICE> persistenceServiceClassArg) {

		super(resourceIdentifier, pojoClassArg, persistenceServiceClassArg);
	}

	@Override
	public void compareObjects(final POJOCLASS expectedObject, final POJOCLASS actualObject) {

		super.compareObjects(expectedObject, actualObject);

		if (expectedObject.getDescription() != null) {

			Assert.assertNotNull("the " + pojoClassName + " description shouldn't be null", actualObject.getDescription());
			Assert.assertEquals("the " + pojoClassName + " descriptions should be equal", expectedObject.getDescription(),
					actualObject.getDescription());
		}
	}
}
