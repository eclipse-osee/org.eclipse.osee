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
package org.eclipse.osee.ats.rest.internal.cpa;

import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.core.cpa.CpaFactory;

/**
 * @author Donald G. dunne
 */
public class CpaUtil {
   private static String cpaBasepath;

   private CpaUtil() {
      // utility class
   }

   public static UriBuilder getCpaPath(AtsApi atsApi) {
      return UriBuilder.fromPath(getCpaBasePath(atsApi)).path("ats").path("cpa").path("decision");
   }

   public static String getCpaBasePath(AtsApi atsApi) {
      if (cpaBasepath == null) {
         cpaBasepath = atsApi.getConfigValue(CpaFactory.CPA_BASEPATH_KEY);
      }
      return cpaBasepath;
   }
}