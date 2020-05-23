/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.jaxrs.server.internal;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsApplicationRegistry;
import org.eclipse.osee.jaxrs.server.internal.resources.JaxRsContributionsResource;

/**
 * @author Roberto E. Escobar
 */
@ApplicationPath("jaxrs-admin")
public class JaxRsAdminApplication extends Application {

   private final Set<Object> singletons = new LinkedHashSet<>();

   private JaxRsApplicationRegistry registry;
   private JaxRsResourceManager manager;

   public void setJaxRsApplicationRegistry(JaxRsApplicationRegistry registry) {
      this.registry = registry;
   }

   public void setJaxRsResourceManager(JaxRsResourceManager manager) {
      this.manager = manager;
   }

   public void start() {
      singletons.add(new JaxRsContributionsResource(registry, manager));
   }

   public void stop() {
      singletons.clear();
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

}