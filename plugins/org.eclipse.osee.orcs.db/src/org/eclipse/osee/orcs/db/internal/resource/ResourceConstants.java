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
package org.eclipse.osee.orcs.db.internal.resource;

import java.io.File;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.orcs.SystemPreferences;

/**
 * @author Roberto E. Escobar
 */
public final class ResourceConstants {

   private ResourceConstants() {
      // Constants class
   }

   public static final String ZIP_EXTENSION = "zip";
   public static final String EXCHANGE_RESOURCE_PROTOCOL = "exchange";
   public static final String ATTRIBUTE_RESOURCE_PROTOCOL = "attr";

   public static String getExchangeDataPath(SystemPreferences preferences)  {
      String binaryDataPath = getBinaryDataPath(preferences);
      return binaryDataPath + File.separator + ResourceConstants.EXCHANGE_RESOURCE_PROTOCOL + File.separator;
   }

   public static String getAttributeDataPath(SystemPreferences preferences)  {
      String binaryDataPath = getBinaryDataPath(preferences);
      return binaryDataPath + File.separator + ResourceConstants.ATTRIBUTE_RESOURCE_PROTOCOL + File.separator;
   }

   public static String getBinaryDataPath(SystemPreferences preferences)  {
      return preferences.getValue(OseeClient.OSEE_APPLICATION_SERVER_DATA);
   }
}
