/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

   public static final String CONFIGURATION_FILE = qualify("config.file");
   public static final String CONFIGURATION_POLL_TIME = qualify("poll.time");
   public static final String CONFIGURATION_POLL_TIME_UNIT = qualify("poll.time.unit");

   public static final long DEFAULT_POLL_TIME = 3L;
   public static final TimeUnit DEFAULT_POLL_TIME_UNIT = TimeUnit.SECONDS;

}
