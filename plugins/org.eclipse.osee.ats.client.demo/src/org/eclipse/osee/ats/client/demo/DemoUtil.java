/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.demo;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.core.OseeInfo;

public class DemoUtil {

   private DemoUtil() {
      // Utility class
   }

   public static void checkDbInitSuccess() throws OseeCoreException {
      if (!isDbInitSuccessful()) {
         throw new OseeStateException("DbInit must be successful to continue");
      }
   }

   public static void checkDbInitAndPopulateSuccess() throws OseeCoreException {
      if (!isDbInitSuccessful()) {
         throw new OseeStateException("DbInit must be successful to continue");
      }
      if (!isPopulateDbSuccessful()) {
         throw new OseeStateException("PopulateDb must be successful to continue");
      }
   }

   public static boolean isDbInitSuccessful() throws OseeCoreException {
      return OseeInfo.isBoolean("DbInitSuccess");
   }

   public static void setDbInitSuccessful(boolean success) throws OseeCoreException {
      OseeInfo.setBoolean("DbInitSuccess", success);
   }

   public static boolean isPopulateDbSuccessful() throws OseeCoreException {
      return OseeInfo.isBoolean("PopulateSuccessful");
   }

   public static void setPopulateDbSuccessful(boolean success) throws OseeCoreException {
      OseeInfo.setBoolean("PopulateSuccessful", success);
   }
}
