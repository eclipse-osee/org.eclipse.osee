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
package org.eclipse.osee.orcs.db.intergration;

import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.core.ds.DataStoreConstants;
import org.eclipse.osee.orcs.db.internal.accessor.OseeInfoDataAccessor;
import org.eclipse.osee.orcs.db.mocks.MockLog;

/**
 * Test Case for {@link OseeInfoDataAccessor}
 *
 * @author Roberto E. Escobar
 */
public class OseeInfoDataAccessorTest {

   @org.junit.Test(expected = OseeStateException.class)
   public void testSetBinaryDataPath() {
      OseeInfoDataAccessor accessor = new OseeInfoDataAccessor();
      accessor.setLogger(new MockLog());

      accessor.putValue(OseeClient.OSEE_APPLICATION_SERVER_DATA, "dummy");
   }

   @org.junit.Test(expected = OseeStateException.class)
   public void testSetCheckTagQueueOnStartupAllowed() {
      OseeInfoDataAccessor accessor = new OseeInfoDataAccessor();
      accessor.setLogger(new MockLog());

      accessor.putValue(DataStoreConstants.DATASTORE_INDEX_ON_START_UP, "dummy");
   }

}