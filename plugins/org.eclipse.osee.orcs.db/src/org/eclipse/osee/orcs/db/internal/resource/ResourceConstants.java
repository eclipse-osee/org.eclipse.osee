/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.resource;

import java.io.File;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.orcs.SystemProperties;

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

   public static String getExchangeDataPath(SystemProperties preferences) {
      String binaryDataPath = getBinaryDataPath(preferences);
      return binaryDataPath + File.separator + ResourceConstants.EXCHANGE_RESOURCE_PROTOCOL + File.separator;
   }

   public static String getAttributeDataPath(SystemProperties preferences) {
      String binaryDataPath = getBinaryDataPath(preferences);
      return binaryDataPath + File.separator + ResourceConstants.ATTRIBUTE_RESOURCE_PROTOCOL + File.separator;
   }

   public static String getBinaryDataPath(SystemProperties preferences) {
      return preferences.getValue(OseeClient.OSEE_APPLICATION_SERVER_DATA);
   }
}
