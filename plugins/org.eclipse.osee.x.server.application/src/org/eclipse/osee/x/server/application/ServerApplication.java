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
package org.eclipse.osee.x.server.application;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.x.server.application.internal.ServerHealthEndpointImpl;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
@ApplicationPath("server")
public class ServerApplication extends Application {

   private final Set<Object> singletons = new HashSet<Object>();
   private IApplicationServerManager applicationServerManager;
   private final Map<String, JdbcService> jdbcServices = new ConcurrentHashMap<>();
   private IAuthenticationManager authManager;

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
      singletons.add(new ServerHealthEndpointImpl(applicationServerManager, jdbcServices, authManager));
   }

   public void stop() {
      singletons.clear();
   }

}
