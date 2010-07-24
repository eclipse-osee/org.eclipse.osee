/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.server.test.internal.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.test.cache.AbstractOseeCacheTest;
import org.eclipse.osee.framework.core.model.test.mocks.MockOseeDataAccessor;
import org.eclipse.osee.framework.core.server.internal.session.Session;
import org.eclipse.osee.framework.core.server.internal.session.SessionCache;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test Case for {@link SessionCacheTest}
 * 
 * @author Roberto E. Escobar
 */
public class SessionCacheTest extends AbstractOseeCacheTest<Session> {

	private static AbstractOseeCache<Session> cache;
	private static List<Session> sessionData;

	@BeforeClass
	public static void prepareTestData() throws OseeCoreException {
		sessionData = new ArrayList<Session>();

		SessionDataAccessor accessor = new SessionDataAccessor(sessionData);
		cache = new SessionCache(accessor);
		cache.ensurePopulated();

		Assert.assertTrue(accessor.wasLoaded());
	}

	public SessionCacheTest() {
		super(sessionData, cache);
	}

	private final static class SessionDataAccessor extends MockOseeDataAccessor<Session> {

		private final List<Session> data;

		public SessionDataAccessor(List<Session> data) {
			super();
			this.data = data;
		}

		@Override
		public void load(IOseeCache<Session> cache) throws OseeCoreException {
			super.load(cache);
			int typeId = 100;
			for (int index = 0; index < 10; index++) {

				String guid = GUID.create();
				Session item =
							new Session(guid, guid, "userId", new Date(), "serverX", "clientVersion", "clientMachine",
										"clientAddress", 4500, new Date(), "Test Data");
				item.setStorageState(StorageState.LOADED);
				data.add(item);
				item.setId(typeId++);
				cache.cache(item);
			}
		}
	}
}
