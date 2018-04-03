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
package org.eclipse.osee.framework.core.client.internal;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpServer;
import org.osgi.framework.BundleContext;

/**
 * KEEP<br/>
 * Start and stop the client httpServer and release the session upon stop.
 *
 * @author Donald G. Dunne
 */
public class Activator extends Plugin {

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
      ClientSessionManager.releaseSession();
      context = null;
   }

   public static Activator getInstance() {
      return instance;
   }

}