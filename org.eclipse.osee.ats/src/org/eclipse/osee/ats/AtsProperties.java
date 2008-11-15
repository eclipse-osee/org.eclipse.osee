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
package org.eclipse.osee.ats;

/**
 * @author Roberto E. Escobar
 */
public class AtsProperties {

   private static final String ATS_IGNORE_CONFIG_UPGRADES = "osee.ats.ignore.config.upgrades";
   private static final String ATS_DISABLE_EMAIL = "osee.ats.disable.email";
   private static final String ATS_ALWAYS_EMAIL_ME = "osee.ats.always.email.me";

   private AtsProperties() {
   }

   public static boolean isAtsIgnoreConfigUpgrades() {
      return System.getProperty(ATS_IGNORE_CONFIG_UPGRADES) != null;
   }

   public static boolean isAtsDisableEmail() {
      return System.getProperty(ATS_DISABLE_EMAIL) != null;
   }

   public static boolean isAtsAlwaysEmailMe() {
      return System.getProperty(ATS_ALWAYS_EMAIL_ME) != null;
   }
}
