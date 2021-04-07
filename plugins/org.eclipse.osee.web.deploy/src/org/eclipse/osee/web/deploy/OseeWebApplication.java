/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.web.deploy;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Get application.wadl at this context to get rest documentation
 *
 * @author Ryan Baldwin
 */
@ApplicationPath("osee")
public class OseeWebApplication extends Application {

   private final Set<Object> resources = new HashSet<>();

   public void start() {
      resources.add(new OseeWebEndpoint());
   }

   public void stop() {
      resources.clear();
   }

   @Override
   public Set<Object> getSingletons() {
      return resources;
   }
}