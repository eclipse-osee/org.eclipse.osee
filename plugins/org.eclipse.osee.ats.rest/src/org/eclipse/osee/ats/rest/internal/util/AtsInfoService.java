/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util;

import javax.ws.rs.core.Application;
import org.eclipse.osee.http.jetty.JettyConfig;
import org.eclipse.osee.http.jetty.JettyHttpService;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public class AtsInfoService extends Application {

   private Log logger;
   private JettyHttpService httpService;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setJettyHttpService(JettyHttpService httpService) {
      this.httpService = httpService;
   }

   public void start() {
      try {
         if (httpService != null) {
            JettyConfig config = httpService.getConfig();
            logger.info("App Server - http://localhost:%d", config.getHttpPort());
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

}
