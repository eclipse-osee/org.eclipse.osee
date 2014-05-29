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
package org.eclipse.osee.framework.manager.servlet.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;

/**
 * @author Donald G. Dunne
 */
@ApplicationPath("oseex")
public class ServerApplication extends Application {

   private final Set<Object> resources = new HashSet<Object>();

   private IApplicationServerManager serverManager;

   public void setServerManager(IApplicationServerManager serverManager) {
      this.serverManager = serverManager;
   }

   public void start() {
      resources.add(new ClientResource(serverManager));
   }

   public void stop() {
      resources.clear();
   }

   @Override
   public Set<Object> getSingletons() {
      return resources;
   }

}
