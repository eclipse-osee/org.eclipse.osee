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
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.impl.resource.AtsResourceTokens;
import org.eclipse.osee.ats.rest.internal.build.report.resources.BuildTraceReportResource;
import org.eclipse.osee.ats.rest.internal.resources.ActionResource;
import org.eclipse.osee.ats.rest.internal.resources.ConvertResource;
import org.eclipse.osee.ats.rest.internal.util.JaxRsExceptionMapper;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author John Misinco
 */
public class AtsApplication extends Application {

   private final Set<Object> singletons = new HashSet<Object>();

   private IAtsServer atsServer;
   private OrcsApi orcsApi;
   private Log logger;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setAtsServer(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   public void start() {
      IResourceRegistry registry = orcsApi.getResourceRegistry();

      AtsResourceTokens.register(registry);
      AtsRestTemplateTokens.register(registry);

      singletons.add(new JaxRsExceptionMapper(registry));

      singletons.add(new BuildTraceReportResource(logger, orcsApi));

      singletons.add(new ActionResource(atsServer, orcsApi));
      singletons.add(new ConvertResource(orcsApi));
      System.out.println("ATS - AtsApplication started");
   }

   public void stop() {
      singletons.clear();
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }
}
