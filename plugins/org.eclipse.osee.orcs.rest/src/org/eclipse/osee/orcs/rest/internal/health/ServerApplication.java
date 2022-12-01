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

package org.eclipse.osee.orcs.rest.internal.health;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
@ApplicationPath("server")
public class ServerApplication extends Application {

   private final Set<Object> singletons = new HashSet<>();
   private IApplicationServerManager applicationServerManager;
   private final Map<String, JdbcService> jdbcServices = new ConcurrentHashMap<>();
   private IAuthenticationManager authManager;
   private ActivityLog activityLog;
   private OrcsApi orcsApi;

   public void setActivityLog(ActivityLog activityLog) {
      this.activityLog = activityLog;
   }

   public void setAuthenticationManager(IAuthenticationManager authManager) {
      this.authManager = authManager;
   }

   public void setApplicationServerManager(IApplicationServerManager applicationServerManager) {
      this.applicationServerManager = applicationServerManager;
   }

   public void addJdbcService(JdbcService jdbcService) {
      jdbcServices.put(jdbcService.getId(), jdbcService);
   }

   public void removeJdbcService(JdbcService jdbcService) {
      jdbcServices.remove(jdbcService.getId());
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

   public void start(Map<String, Object> properties) {
      singletons.add(
         new ServerHealthEndpointImpl(orcsApi, applicationServerManager, jdbcServices, authManager, activityLog));
   }

   public void stop() {
      singletons.clear();
   }

   public OrcsApi getOrcsApi() {
      return orcsApi;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

}
