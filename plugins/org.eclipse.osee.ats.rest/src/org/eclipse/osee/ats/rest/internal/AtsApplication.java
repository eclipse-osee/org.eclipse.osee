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
package org.eclipse.osee.ats.rest.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;
import org.eclipse.osee.ats.rest.internal.build.report.resources.BuildTraceReportResource;
import org.eclipse.osee.ats.rest.internal.resources.ProgramResource;
import org.eclipse.osee.ats.rest.internal.resources.ProgramsResource;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author John Misinco
 */
public class AtsApplication extends Application {

   private static OrcsApi orcsApi;
   private static Log logger;

   public void setOrcsApi(OrcsApi orcsApi) {
      AtsApplication.orcsApi = orcsApi;
   }

   public static OrcsApi getOrcsApi() {
      return orcsApi;
   }

   public static Log getLogger() {
      return logger;
   }

   public void setLogger(Log logger) {
      AtsApplication.logger = logger;
   }

   @Override
   public Set<Class<?>> getClasses() {
      Set<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(ProgramsResource.class);
      classes.add(ProgramResource.class);
      classes.add(BuildTraceReportResource.class);
      return classes;
   }

}
