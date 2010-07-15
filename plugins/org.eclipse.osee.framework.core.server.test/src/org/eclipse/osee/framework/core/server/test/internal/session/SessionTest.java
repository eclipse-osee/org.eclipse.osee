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
import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.model.test.type.AbstractOseeTypeTest;
import org.eclipse.osee.framework.core.server.internal.session.Session;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link Session}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class SessionTest extends AbstractOseeTypeTest<Session> {

	private final int expectedId;

	public SessionTest(Session type, int expectedId, String guid, String name) {
		super(type, guid, name);
		this.expectedId = expectedId;
	}

	@Test
	public void testGuidAsInteger() {
		int actualId = Session.guidAsInteger(getType().getGuid());
		Assert.assertEquals(expectedId, actualId);
	}

	@Parameters
	public static Collection<Object[]> getData() {
		Collection<Object[]> data = new ArrayList<Object[]>();
		for (int index = 1; index <= 3; index++) {
			String guid = "ABCD" + String.valueOf(index);
			int actualIndex = index - 1;
			int expectedId = 1946 + (actualIndex * 20 + 4 * actualIndex);

			String name = "index: " + index;

			Session session =
						new Session(guid, name, "userId", new Date(), "serverX", "clientVersion", "clientMachine",
									"clientAddress", 4500, new Date(), "Test Data");
			session.setStorageState(StorageState.LOADED);

			session.clearDirty();

			data.add(new Object[] {session, expectedId, guid, name});
		}
		return data;
	}
}
