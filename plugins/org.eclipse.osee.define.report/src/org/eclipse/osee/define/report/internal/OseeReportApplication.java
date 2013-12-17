/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;
import org.eclipse.osee.define.report.OseeDefineResourceTokens;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 */
public final class OseeReportApplication extends Application {
   private OrcsApi orcsApi;
   private final Set<Object> singletons = new HashSet<Object>();
   private Log logger;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void start() {
      OseeDefineResourceTokens.register(orcsApi.getResourceRegistry());
      singletons.add(new RequirementResource(orcsApi));
      singletons.add(new SystemSafetyResource(logger, orcsApi));
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }
}
