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

package org.eclipse.osee.framework.server.ide.internal;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.server.ide.AdminServlet;
import org.eclipse.osee.framework.server.ide.SessionClientLoopbackServlet;
import org.eclipse.osee.logger.Log;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;

/**
 * @author Roberto E. Escobar
 */
public class ServletRegistrationHandler {

   private HttpService httpService;
   private Log logger;
   private ISessionManager sessionManager;

   private final Set<String> contexts = new HashSet<>();

   public void setSessionManager(ISessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public Log getLogger() {
      return logger;
   }

   public void setHttpService(HttpService httpService) {
      this.httpService = httpService;
   }

   public synchronized void start(BundleContext context) {
      ServletUtil.unregister(httpService, contexts);
      registerServices(context);
   }

   public synchronized void stop() {
      ServletUtil.unregister(httpService, contexts);
      contexts.clear();
   }

   private void registerServices(BundleContext context) {
      contexts.clear();
      register(new SessionClientLoopbackServlet(logger, sessionManager), OseeServerContext.CLIENT_LOOPBACK_CONTEXT);

      register(new AdminServlet(logger, context), "osee/console");
   }

   private void register(OseeHttpServlet servlet, String contexts) {
      this.contexts.add(contexts);
      ServletUtil.register(httpService, servlet, contexts);
   }

}
