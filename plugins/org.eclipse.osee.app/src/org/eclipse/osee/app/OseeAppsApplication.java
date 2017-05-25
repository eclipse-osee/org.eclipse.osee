/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.app;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author David W. Miller
 */
@ApplicationPath("apps")
public final class OseeAppsApplication extends Application {
   private final Set<Object> singletons = new HashSet<>();
   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void start(Map<String, Object> properties) {
      singletons.add(new OseeAppResource(orcsApi));
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }
}
