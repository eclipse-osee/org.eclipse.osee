/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.config.admin.internal;

import java.util.concurrent.TimeUnit;

/**
 * Class to collect configuration constants such as keys and default values.
 * 
 * @author Roberto E. Escobar
 */
public final class ConfigManagerConstants {

   private ConfigManagerConstants() {
      // Constants class
   }

   public static final String NAMESPACE = "cm";

   private static String qualify(String value) {
      return String.format("%s.%s", NAMESPACE, value);
   }

   public static final String CONFIGURATION_URI = qualify("config.uri");
   public static final String CONFIGURATION_POLL_TIME = qualify("poll.time");
   public static final String CONFIGURATION_POLL_TIME_UNIT = qualify("poll.time.unit");

   public static final long DEFAULT_POLL_TIME = 3L;
   public static final TimeUnit DEFAULT_POLL_TIME_UNIT = TimeUnit.SECONDS;

}
