/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsApplicationRegistry;
import org.eclipse.osee.jaxrs.server.internal.resources.ApplicationResource;

/**
 * @author Roberto E. Escobar
 */
@ApplicationPath("jaxrs-admin")
public class JaxRsAdminApplication extends Application {

   private final Set<Object> singletons = new LinkedHashSet<Object>();

   private JaxRsApplicationRegistry registry;

   public void setJaxRsApplicationRegistry(JaxRsApplicationRegistry registry) {
      this.registry = registry;
   }

   public void start() {
      singletons.add(new ApplicationResource(registry));
   }

   public void stop() {
      singletons.clear();
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

}