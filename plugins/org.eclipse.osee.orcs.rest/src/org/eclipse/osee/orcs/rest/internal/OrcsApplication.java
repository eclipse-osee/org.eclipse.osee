/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.admin.LinkUpdateResource;
import org.eclipse.osee.orcs.rest.internal.applicability.ApplicabilityUiEndpointImpl;
import org.eclipse.osee.orcs.rest.internal.writer.OrcsWriterEndpointImpl;
import org.osgi.service.event.EventAdmin;

/**
 * Get application.wadl at this context to get rest documentation
 *
 * @author Roberto E. Escobar
 */
@ApplicationPath("orcs")
public class OrcsApplication extends Application {

   private final Set<Object> resources = new HashSet<>();
   private OrcsApi orcsApi;
   private IResourceManager resourceManager;
   private ActivityLog activityLog;
   private JdbcService jdbcService;
   private EventAdmin eventAdmin;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void setActivityLog(ActivityLog activityLog) {
      this.activityLog = activityLog;
   }

   public void setEventAdmin(EventAdmin eventAdmin) {
      this.eventAdmin = eventAdmin;
   }

   public void setResourceManager(IResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }

   public void start() {
      resources.add(new BranchesResource(orcsApi));
      resources.add(new OrcsScriptEndpointImpl(orcsApi.getScriptEngine()));
      resources.add(new BranchEndpointImpl(orcsApi, resourceManager, activityLog));
      resources.add(new ApplicabilityUiEndpointImpl(orcsApi));
      resources.add(new OrcsWriterEndpointImpl(orcsApi));
      resources.add(new TransactionEndpointImpl(orcsApi));
      resources.add(new TypesEndpointImpl(orcsApi, jdbcService));

      resources.add(new IndexerEndpointImpl(orcsApi));
      resources.add(new ResourcesEndpointImpl(resourceManager));
      resources.add(new DatastoreEndpointImpl(orcsApi, activityLog));
      resources.add(new KeyValueResource(orcsApi));

      resources.add(new LinkUpdateResource(orcsApi));
   }

   public void stop() {
      resources.clear();
   }

   @Override
   public Set<Object> getSingletons() {
      return resources;
   }
}