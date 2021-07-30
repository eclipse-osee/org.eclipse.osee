/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.rest.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.TransactionBuilderMessageReader;
import org.eclipse.osee.orcs.rest.admin.LinkUpdateResource;
import org.eclipse.osee.orcs.rest.internal.applicability.ApplicabilityUiEndpointImpl;
import org.eclipse.osee.orcs.rest.internal.types.TypesEndpointImpl;
import org.eclipse.osee.orcs.rest.internal.writer.OrcsWriterEndpointImpl;
import org.osgi.service.event.EventAdmin;

/**
 * Get application.wadl at this context to get rest documentation
 *
 * @author Roberto E. Escobar
 */
@ApplicationPath("orcs")
public class OrcsApplication extends Application {

   private final Set<Object> singletons = new HashSet<>();
   private OrcsApi orcsApi;
   private IResourceManager resourceManager;
   private ActivityLog activityLog;
   private JdbcService jdbcService;

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
      //do nothing
   }

   public void setResourceManager(IResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }

   public void start() {
      // Add all root resource, provider and feature instances.
      singletons.add(new BranchesResource(orcsApi));
      singletons.add(new BranchEndpointImpl(orcsApi, resourceManager, activityLog));
      singletons.add(new ApplicabilityUiEndpointImpl(orcsApi));
      singletons.add(new OrcsWriterEndpointImpl(orcsApi));
      singletons.add(new TransactionEndpointImpl(orcsApi));
      singletons.add(new TypesEndpointImpl(orcsApi, jdbcService));

      singletons.add(new IndexerEndpointImpl(orcsApi));
      singletons.add(new ResourcesEndpointImpl(resourceManager));
      singletons.add(new DatastoreEndpointImpl(orcsApi, activityLog));
      singletons.add(new KeyValueResource(orcsApi));

      singletons.add(new LinkUpdateResource(orcsApi));
      singletons.add(new ReportEndpointImpl(orcsApi));
      singletons.add(new TransactionBuilderMessageReader(orcsApi));
   }

   public void stop() {
      singletons.clear();
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }
}