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

package org.eclipse.osee.framework.core.client.internal;

import org.eclipse.osee.framework.core.client.server.HttpServer;
import org.eclipse.osee.framework.plugin.core.OseeActivator;
import org.osgi.framework.BundleContext;

/**
 * KEEP<br/>
 * Start and stop the client httpServer and release the session upon stop.
 *
 * @author Donald G. Dunne
 */
public class Activator extends OseeActivator {

   private static Activator instance;

   public Activator() {
      instance = this;
   }

   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      HttpServer.startServer(1);
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
      HttpServer.stopServer();
      context = null;
   }

   public static Activator getInstance() {
      return instance;
   }

}