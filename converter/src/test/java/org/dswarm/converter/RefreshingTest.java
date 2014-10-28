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
package org.dswarm.converter;

import org.junit.Before;

import org.dswarm.converter.GuicedTest;
import org.dswarm.persistence.service.MaintainDBService;

/**
 * Cleans the databases before running the tests. Override initObjects() in sub-classes to 
 * (re)init other members than the maintain DB service itself as well.
 * 
 * @author polowins
 */
public abstract class RefreshingTest extends GuicedTest {

	protected MaintainDBService	maintainDBService;

	public RefreshingTest() {
		initObjects();
	}

	@Before
	public void prepare() throws Exception {
		GuicedTest.tearDown();
		GuicedTest.startUp();
		initObjects();
		maintainDBService.initDB();
	}
	
	protected void initObjects() {
		maintainDBService = GuicedTest.injector.getInstance(MaintainDBService.class);
	}

}
