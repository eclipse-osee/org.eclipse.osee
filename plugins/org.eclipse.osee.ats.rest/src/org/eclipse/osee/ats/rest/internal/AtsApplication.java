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
import org.eclipse.osee.ats.rest.internal.agile.AgileEndpointImpl;
import org.eclipse.osee.ats.rest.internal.config.ActionableItemResource;
import org.eclipse.osee.ats.rest.internal.config.ConvertResource;
import org.eclipse.osee.ats.rest.internal.config.ProgramResource;
import org.eclipse.osee.ats.rest.internal.config.TeamResource;
import org.eclipse.osee.ats.rest.internal.config.UserResource;
import org.eclipse.osee.ats.rest.internal.config.VersionResource;
import org.eclipse.osee.ats.rest.internal.cpa.CpaResource;
import org.eclipse.osee.ats.rest.internal.cpa.CpaServiceRegistry;
import org.eclipse.osee.ats.rest.internal.workitem.ActionResource;
import org.eclipse.osee.ats.rest.internal.workitem.ActionUiResource;
import org.eclipse.osee.ats.rest.internal.workitem.StateResource;
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
      OseeTemplateTokens.register(registry);

      // Agile resources
      singletons.add(new AgileEndpointImpl(atsServer));

      // Config resources
      singletons.add(new VersionResource(atsServer));
      singletons.add(new TeamResource(atsServer));
      singletons.add(new ProgramResource(atsServer));
      singletons.add(new ActionableItemResource(atsServer));

      singletons.add(new ActionResource(atsServer, orcsApi));
      singletons.add(new StateResource(atsServer));
      singletons.add(new ConvertResource(atsServer));
      singletons.add(new CpaResource(orcsApi, atsServer, cpaRegistry));
      singletons.add(new UserResource(atsServer.getUserService()));

      singletons.add(new AtsEndpointImpl(atsServer, logger, registry, cpaRegistry));

      singletons.add(new ActionUiResource(atsServer, logger));
   }

   public void stop() {
      singletons.clear();
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

}
