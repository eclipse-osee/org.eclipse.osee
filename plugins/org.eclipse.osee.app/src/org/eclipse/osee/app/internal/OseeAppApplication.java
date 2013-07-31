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
package org.eclipse.osee.app.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;
import org.eclipse.osee.app.OseeAppResourceTokens;
import org.eclipse.osee.framework.core.server.IApplicationServerLookup;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 */
public final class OseeAppApplication extends Application {
   private OrcsApi orcsApi;
   private IApplicationServerLookup lookupService;
   private final Set<Object> singletons = new HashSet<Object>();

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setServerLookup(IApplicationServerLookup lookupService) {
      this.lookupService = lookupService;
   }

   public void start() {
      IResourceRegistry registry = orcsApi.getResourceRegistry();
      OseeAppResourceTokens.register(registry);
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

}
