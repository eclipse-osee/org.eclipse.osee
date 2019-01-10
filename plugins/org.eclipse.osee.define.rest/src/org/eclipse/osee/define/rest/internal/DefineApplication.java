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
package org.eclipse.osee.define.rest.internal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.app.OseeAppResourceTokens;
import org.eclipse.osee.define.api.DefineApi;
import org.eclipse.osee.define.rest.DataRightsEndpointImpl;
import org.eclipse.osee.define.rest.ImportEndpointImpl;
import org.eclipse.osee.define.rest.MSWordEndpointImpl;
import org.eclipse.osee.define.rest.TraceabilityEndpointImpl;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceRegistry;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 */
@ApplicationPath("define")
public final class DefineApplication extends Application {
   private final Set<Object> singletons = new HashSet<>();
   private OrcsApi orcsApi;
   private DefineApi defineApi;
   private ActivityLog activityLog;
   private JdbcService jdbcService;

   public void setDefineApi(DefineApi defineApi) {
      this.defineApi = defineApi;
   }

   public void setActivityLog(ActivityLog activityLog) {
      this.activityLog = activityLog;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void start(Map<String, Object> properties) {
      IResourceRegistry resourceRegistry = new ResourceRegistry();
      OseeAppResourceTokens.register(resourceRegistry);
      JdbcClient jdbcClient = jdbcService.getClient();
      singletons.add(new SystemSafetyResource(activityLog, resourceRegistry, orcsApi));
      singletons.add(new TraceabilityEndpointImpl(activityLog, resourceRegistry, orcsApi, defineApi));
      singletons.add(new DataRightsSwReqAndCodeResource(activityLog, resourceRegistry, orcsApi));
      singletons.add(new DataRightsEndpointImpl(defineApi));
      singletons.add(new MSWordEndpointImpl(defineApi));
      singletons.add(new DefineBranchEndpointImpl(jdbcClient, orcsApi));
      singletons.add(new ImportEndpointImpl(defineApi));
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }
}
