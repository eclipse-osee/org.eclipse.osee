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
import org.eclipse.osee.ats.rest.internal.resources.ActionResource;
import org.eclipse.osee.ats.rest.internal.resources.AtsUiResource;
import org.eclipse.osee.ats.rest.internal.resources.ConfigResource;
import org.eclipse.osee.ats.rest.internal.resources.ConvertResource;
import org.eclipse.osee.ats.rest.internal.resources.TeamResource;
import org.eclipse.osee.ats.rest.internal.resources.UserResource;
import org.eclipse.osee.ats.rest.internal.resources.VersionResource;
import org.eclipse.osee.ats.rest.internal.util.JaxRsExceptionMapper;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
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

   private IAtsServer atsServer;
   private OrcsApi orcsApi;
   private Log logger;
   private IApplicationServerManager appServerMgr;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setAtsServer(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   public void setAppServerMgr(IApplicationServerManager appServerMgr) {
      this.appServerMgr = appServerMgr;
   }

   public void start() {
      IResourceRegistry registry = new ResourceRegistry();
      AtsResourceTokens.register(registry);
      AtsRestTemplateTokens.register(registry);
      OseeTemplateTokens.register(registry);

      singletons.add(new JaxRsExceptionMapper(registry));

      singletons.add(new BuildTraceReportResource(logger, registry, orcsApi));

      singletons.add(new ActionResource(atsServer, orcsApi, registry));
      singletons.add(new ConvertResource(registry));
      singletons.add(new TeamResource(orcsApi));
      singletons.add(new VersionResource(orcsApi));
      singletons.add(new ConfigResource(atsServer, orcsApi, logger, registry));
      singletons.add(new CpaResource(orcsApi, atsServer, appServerMgr));
      singletons.add(new UserResource(atsServer.getUserService()));

      singletons.add(new AtsUiResource(registry, orcsApi));
      System.out.println("ATS - Application started - " + appServerMgr.getServerUri());
   }

   public void stop() {
      singletons.clear();
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

}
