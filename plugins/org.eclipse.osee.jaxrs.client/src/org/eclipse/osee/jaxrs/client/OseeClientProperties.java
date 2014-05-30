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
package org.eclipse.osee.jaxrs.client;

/**
 * @author Roberto E. Escobar
 */
public final class OseeClientProperties {

   private OseeClientProperties() {
      // Utility class
   }

   private static final String OSEE_APPLICATION_SERVER = "osee.application.server";

   public static String getApplicationServerAddress() {
      String appServer = System.getProperty(OSEE_APPLICATION_SERVER, "");
      return appServer;
   }

}
