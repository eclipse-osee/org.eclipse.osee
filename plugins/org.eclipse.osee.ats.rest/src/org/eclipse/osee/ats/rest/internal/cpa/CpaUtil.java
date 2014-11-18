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
import org.eclipse.osee.ats.core.cpa.CpaFactory;
import org.eclipse.osee.ats.impl.IAtsServer;

/**
 * @author Donald G. dunne
 */
public class CpaUtil {
   private static String cpaBasepath;

   private CpaUtil() {
      // utility class
   }

   public static UriBuilder getCpaPath(IAtsServer atsServer) {
      return UriBuilder.fromPath(getCpaBasePath(atsServer)).path("ats").path("cpa").path("decision");
   }

   public static String getCpaBasePath(IAtsServer atsServer) {
      if (cpaBasepath == null) {
         cpaBasepath = atsServer.getConfigValue(CpaFactory.CPA_BASEPATH_KEY);
      }
      return cpaBasepath;
   }

}
