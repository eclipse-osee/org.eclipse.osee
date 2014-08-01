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
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.impl.resource.AtsResourceTokens;
import org.eclipse.osee.ats.rest.internal.build.report.resources.BuildTraceReportResource;
import org.eclipse.osee.ats.rest.internal.cpa.CpaResource;
import org.eclipse.osee.ats.rest.internal.cpa.CpaServiceRegistry;
import org.eclipse.osee.ats.rest.internal.resources.ActionResource;
import org.eclipse.osee.ats.rest.internal.resources.AtsUiResource;
import org.eclipse.osee.ats.rest.internal.resources.ConvertResource;
import org.eclipse.osee.ats.rest.internal.resources.TeamResource;
import org.eclipse.osee.ats.rest.internal.resources.UserResource;
import org.eclipse.osee.ats.rest.internal.resources.VersionResource;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceRegistry;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.template.engine.OseeTemplateTokens;

/**
 * @author John Misinco
 */
@ApplicationPath("ats")
public class AtsApplication extends Application {

   private final Set<Object> singletons = new HashSet<Object>();

   private Log logger;
   private OrcsApi orcsApi;
   private IAtsServer atsServer;
   private CpaServiceRegistry cpaRegistry;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setAtsServer(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   public void setCpaServiceRegistry(CpaServiceRegistry cpaRegistry) {
      this.cpaRegistry = cpaRegistry;
   }

   public void start() {
      IResourceRegistry registry = new ResourceRegistry();
      AtsResourceTokens.register(registry);
      AtsRestTemplateTokens.register(registry);
      OseeTemplateTokens.register(registry);

      singletons.add(new BuildTraceReportResource(logger, registry, orcsApi));
      singletons.add(new ActionResource(atsServer, orcsApi, registry));
      singletons.add(new ConvertResource(atsServer, registry));
      singletons.add(new TeamResource(atsServer));
      singletons.add(new VersionResource(atsServer));
      singletons.add(new CpaResource(orcsApi, atsServer, cpaRegistry));
      singletons.add(new UserResource(atsServer.getUserService()));

      singletons.add(new AtsEndpointImpl(atsServer, logger, registry));

      singletons.add(new AtsUiResource(registry, atsServer));
      System.out.println("ATS - Application started - " + System.getProperty("OseeApplicationServer"));
   }

   public void stop() {
      singletons.clear();
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

}
