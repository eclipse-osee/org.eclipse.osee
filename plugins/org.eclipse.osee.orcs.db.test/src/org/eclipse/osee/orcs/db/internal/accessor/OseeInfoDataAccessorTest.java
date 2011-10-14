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
package org.eclipse.osee.orcs.db.internal.accessor;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.orcs.db.internal.resource.ResourceConstants;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiUtil;
import org.eclipse.osee.orcs.db.mocks.MockLog;
import org.junit.Assert;
import org.junit.Rule;

/**
 * Test Case for {@link OseeInfoDataAccessor}
 * 
 * @author Roberto E. Escobar
 */
public class OseeInfoDataAccessorTest {

   @Rule
   public OseeDatabase db = new OseeDatabase("osee.demo.h2");

   @org.junit.Test
   public void testGetSetValue() throws OseeCoreException {
      IOseeDatabaseService dbService = OsgiUtil.getService(IOseeDatabaseService.class);

      OseeInfoDataAccessor accessor = new OseeInfoDataAccessor();
      accessor.setLogger(new MockLog());
      accessor.setDatabaseService(dbService);

      String value = accessor.getValue("test.data");
      Assert.assertEquals("", value);

      boolean wasSuccessful = accessor.putValue("test.data", "testing 1,2,3");
      Assert.assertTrue(wasSuccessful);

      String value1 = accessor.getValue("test.data");
      Assert.assertEquals("testing 1,2,3", value1);
   }

   @org.junit.Test(expected = OseeStateException.class)
   public void testSetBinaryDataPath() throws OseeCoreException {
      IOseeDatabaseService dbService = OsgiUtil.getService(IOseeDatabaseService.class);

      OseeInfoDataAccessor accessor = new OseeInfoDataAccessor();
      accessor.setLogger(new MockLog());
      accessor.setDatabaseService(dbService);

      accessor.putValue(ResourceConstants.BINARY_DATA_PATH, "dummy");
   }

   @org.junit.Test
   public void testGetBinaryDataPath() throws OseeCoreException {
      IOseeDatabaseService dbService = OsgiUtil.getService(IOseeDatabaseService.class);

      OseeInfoDataAccessor accessor = new OseeInfoDataAccessor();
      accessor.setLogger(new MockLog());
      accessor.setDatabaseService(dbService);

      String original = accessor.getValue(ResourceConstants.BINARY_DATA_PATH);
      Assert.assertEquals(System.getProperty(ResourceConstants.BINARY_DATA_PATH), original);

      System.setProperty(ResourceConstants.BINARY_DATA_PATH, "");
      try {
         String actual = accessor.getValue(ResourceConstants.BINARY_DATA_PATH);
         Assert.assertEquals(System.getProperty("user.home"), actual);
      } finally {
         System.setProperty(ResourceConstants.BINARY_DATA_PATH, original);
      }
   }
}